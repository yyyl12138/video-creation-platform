package com.huike.video.modules.payment.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huike.video.modules.payment.entity.PaymentOrder;
import com.huike.video.modules.payment.mapper.PaymentOrderMapper;
import com.huike.video.modules.payment.service.AlipayService;
import com.huike.video.modules.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单对账定时任务
 * 针对掉单场景，定期主动查询支付宝交易状态
 *
 * 策略:
 * 1. 每 60 秒执行一次
 * 2. 查询创建超过 5 分钟且状态仍为 PENDING 的订单
 * 3. 主动调用支付宝查询接口
 * 4. 若支付宝返回 TRADE_SUCCESS，触发补单流程
 * 5. 若已过期（超过 15 分钟），自动标记为 CANCELLED
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReconciliationTask {

    private final PaymentOrderMapper paymentOrderMapper;
    private final AlipayService alipayService;
    private final OrderService orderService;

    /**
     * 每 60 秒执行一次对账
     */
    @Scheduled(fixedDelay = 60000)
    public void reconcileOrders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusMinutes(5);

        List<PaymentOrder> pendingOrders = paymentOrderMapper.selectList(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getStatus, "PENDING")
                        .le(PaymentOrder::getCreatedAt, threshold)
                        .last("LIMIT 50")
        );

        if (pendingOrders.isEmpty()) {
            return;
        }

        log.info("对账任务开始, 待检查订单数={}", pendingOrders.size());

        for (PaymentOrder order : pendingOrders) {
            try {
                // 检查是否已过期
                if (order.getExpireTime() != null && now.isAfter(order.getExpireTime())) {
                    log.info("订单已过期, 自动取消, orderNo={}", order.getOrderNo());
                    order.setStatus("CANCELLED");
                    paymentOrderMapper.updateById(order);
                    continue;
                }

                // 主动查询支付宝状态
                String tradeStatus = alipayService.queryTradeStatus(order.getOrderNo());
                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    log.info("对账发现掉单, 执行补单, orderNo={}", order.getOrderNo());
                    orderService.completeOrderByReconciliation(order.getOrderNo());
                } else if ("TRADE_CLOSED".equals(tradeStatus)) {
                    log.info("支付宝交易已关闭, 取消订单, orderNo={}", order.getOrderNo());
                    order.setStatus("CANCELLED");
                    paymentOrderMapper.updateById(order);
                }
            } catch (Exception e) {
                log.error("对账处理异常, orderNo={}, error={}", order.getOrderNo(), e.getMessage(), e);
            }
        }

        log.info("对账任务完成");
    }
}

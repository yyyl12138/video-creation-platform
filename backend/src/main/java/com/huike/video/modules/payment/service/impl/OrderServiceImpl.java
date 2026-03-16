package com.huike.video.modules.payment.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.payment.config.AlipayProperties;
import com.huike.video.modules.payment.dto.CreateOrderRequest;
import com.huike.video.modules.payment.entity.PaymentOrder;
import com.huike.video.modules.payment.mapper.PaymentOrderMapper;
import com.huike.video.modules.payment.service.AlipayService;
import com.huike.video.modules.payment.service.OrderService;
import com.huike.video.modules.payment.service.VipService;
import com.huike.video.modules.payment.vo.CreateOrderResponse;
import com.huike.video.modules.payment.vo.OrderStatusResponse;
import com.huike.video.modules.user.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 订单服务实现
 * 负责订单的创建、查询、取消和回调处理
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final PaymentOrderMapper paymentOrderMapper;
    private final AlipayService alipayService;
    private final AlipayProperties alipayProperties;
    private final WalletService walletService;
    private final VipService vipService;

    /** 订单有效期 (分钟) */
    private static final int ORDER_EXPIRE_MINUTES = 15;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ======================== 下单 ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CreateOrderResponse createOrder(CreateOrderRequest request) {
        String userId = StpUtil.getLoginIdAsString();

        // 1. 生成唯一订单号
        String orderNo = generateOrderNo();

        // 2. 构建订单标题
        String subject = buildSubject(request.getOrderType(), request.getAmount());

        // 3. 持久化订单记录 (PENDING 状态)
        PaymentOrder order = new PaymentOrder();
        order.setId(IdUtil.simpleUUID());
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setOrderType(request.getOrderType());
        order.setAmount(request.getAmount());
        order.setPayChannel(request.getPayChannel());
        order.setStatus("PENDING");
        order.setBizContent(request.getBizContent());
        order.setSubject(subject);
        order.setExpireTime(LocalDateTime.now().plusMinutes(ORDER_EXPIRE_MINUTES));
        paymentOrderMapper.insert(order);

        log.info("创建订单成功, orderNo={}, userId={}, type={}, amount={}",
                orderNo, userId, request.getOrderType(), request.getAmount());

        // 4. 调用支付宝生成支付表单
        String payForm = alipayService.createPagePayForm(
                orderNo,
                request.getAmount().setScale(2).toPlainString(),
                subject
        );

        // 5. 封装响应
        CreateOrderResponse resp = new CreateOrderResponse();
        resp.setOrderId(orderNo);
        resp.setPayUrl(payForm);
        resp.setExpireTime(order.getExpireTime().format(FORMATTER));
        return resp;
    }

    // ======================== 查询 ========================

    @Override
    public OrderStatusResponse getOrderStatus(String orderId) {
        String userId = StpUtil.getLoginIdAsString();

        PaymentOrder order = getOrderByNo(orderId);
        if (order == null) {
            throw new BusinessException(50001, "订单不存在");
        }
        // 校验是否本人的订单
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(10001, "无权查看此订单");
        }

        OrderStatusResponse resp = new OrderStatusResponse();
        resp.setOrderId(order.getOrderNo());
        resp.setStatus(order.getStatus());
        resp.setPaidTime(order.getPaidTime() == null ? null : order.getPaidTime().format(FORMATTER));
        return resp;
    }

    // ======================== 取消 ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderId) {
        String userId = StpUtil.getLoginIdAsString();

        PaymentOrder order = getOrderByNo(orderId);
        if (order == null) {
            throw new BusinessException(50001, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(10001, "无权操作此订单");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new BusinessException(50001, "仅待支付订单可取消");
        }

        // 乐观锁：仅 PENDING 状态才能取消
        int rows = paymentOrderMapper.update(null,
                new LambdaUpdateWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderNo, orderId)
                        .eq(PaymentOrder::getStatus, "PENDING")
                        .set(PaymentOrder::getStatus, "CANCELLED")
        );

        if (rows > 0) {
            log.info("订单已取消, orderNo={}", orderId);
        }
        return rows > 0;
    }

    // ======================== 支付宝回调 ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean handleAlipayCallback(Map<String, String> params) {
        // 1. RSA2 验签
        if (!alipayService.verifyCallback(params)) {
            log.warn("支付宝回调验签失败");
            return false;
        }

        // 2. 校验 AppID
        String callbackAppId = params.get("app_id");
        if (!alipayProperties.getAppId().equals(callbackAppId)) {
            log.warn("支付宝回调 AppID 不匹配, expected={}, got={}", alipayProperties.getAppId(), callbackAppId);
            return false;
        }

        // 3. 提取关键参数
        String outTradeNo = params.get("out_trade_no");
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");
        String totalAmount = params.get("total_amount");

        log.info("支付宝回调, outTradeNo={}, tradeNo={}, tradeStatus={}, totalAmount={}",
                outTradeNo, tradeNo, tradeStatus, totalAmount);

        // 仅处理 TRADE_SUCCESS
        if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
            log.info("非成功状态，跳过处理, tradeStatus={}", tradeStatus);
            return true; // 返回 success 给支付宝，避免重发
        }

        // 4. 查询本地订单
        PaymentOrder order = getOrderByNo(outTradeNo);
        if (order == null) {
            log.error("回调对应的订单不存在, outTradeNo={}", outTradeNo);
            return false;
        }

        // 5. 幂等校验: 如果订单已经是 PAID 状态，直接返回 success
        if ("PAID".equals(order.getStatus())) {
            log.info("订单已标记为 PAID，跳过重复处理, outTradeNo={}", outTradeNo);
            return true;
        }

        // 6. 金额绝对校验
        BigDecimal callbackAmount = new BigDecimal(totalAmount);
        if (order.getAmount().setScale(2).compareTo(callbackAmount.setScale(2)) != 0) {
            log.error("回调金额不一致! 本地={}, 回调={}, outTradeNo={}",
                    order.getAmount(), callbackAmount, outTradeNo);
            return false;
        }

        // 7. 乐观锁更新订单状态 (防并发)
        int rows = paymentOrderMapper.update(null,
                new LambdaUpdateWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderNo, outTradeNo)
                        .eq(PaymentOrder::getStatus, "PENDING")
                        .set(PaymentOrder::getStatus, "PAID")
                        .set(PaymentOrder::getExternalTradeNo, tradeNo)
                        .set(PaymentOrder::getPaidTime, LocalDateTime.now())
        );

        if (rows == 0) {
            log.warn("订单状态更新失败 (可能已被并发处理), outTradeNo={}", outTradeNo);
            return true; // 不重发
        }

        // 8. 执行业务逻辑 (发货)
        fulfillOrder(order);

        log.info("回调处理完成, outTradeNo={}", outTradeNo);
        return true;
    }

    // ======================== 私有方法 ========================

    /**
     * 订单支付成功后的"发货"逻辑
     * 根据 orderType 执行不同业务
     */
    private void fulfillOrder(PaymentOrder order) {
        switch (order.getOrderType()) {
            case "RECHARGE" -> {
                // 充值: 增加钱包余额
                boolean success = walletService.rechargeBalance(
                        order.getUserId(), order.getAmount(), order.getOrderNo(),
                        "支付宝充值, 订单号: " + order.getOrderNo()
                );
                if (!success) {
                    log.error("充值到钱包失败, orderNo={}, userId={}", order.getOrderNo(), order.getUserId());
                }
            }
            case "VIP_SUBSCRIPTION" -> {
                // 会员订阅: 解析 planId 并调用 VipService 开通/续费
                Integer planId = parsePlanIdFromBizContent(order.getBizContent());
                boolean success = vipService.activateVip(order.getUserId(), planId);
                if (!success) {
                    log.error("VIP 开通失败, orderNo={}, userId={}", order.getOrderNo(), order.getUserId());
                }
            }
            case "TEMPLATE_PURCHASE" -> {
                // TODO: 调用模版市场服务，将模版添加到用户资产
                log.info("模版购买发货, orderNo={}, userId={}, bizContent={}",
                        order.getOrderNo(), order.getUserId(), order.getBizContent());
            }
            default -> log.warn("未知的订单类型, orderType={}", order.getOrderType());
        }
    }

    /**
     * 根据订单号查询订单
     */
    private PaymentOrder getOrderByNo(String orderNo) {
        return paymentOrderMapper.selectOne(
                new LambdaQueryWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderNo, orderNo)
                        .last("LIMIT 1")
        );
    }

    /**
     * 生成系统订单号
     * 格式: ORD + 年月日时分秒 + 6位随机数
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.valueOf((int) (Math.random() * 900000 + 100000));
        return "ORD" + timestamp + random;
    }

    /**
     * 构建订单标题
     */
    private String buildSubject(String orderType, BigDecimal amount) {
        return switch (orderType) {
            case "RECHARGE" -> "视频创作平台-积分充值 " + amount.setScale(2) + "元";
            case "VIP_SUBSCRIPTION" -> "视频创作平台-会员订阅";
            case "TEMPLATE_PURCHASE" -> "视频创作平台-模版购买";
            default -> "视频创作平台-订单";
        };
    }

    // ======================== 内部补单 ========================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeOrderByReconciliation(String orderNo) {
        PaymentOrder order = getOrderByNo(orderNo);
        if (order == null) {
            log.error("补单失败: 订单不存在, orderNo={}", orderNo);
            return false;
        }

        // 幂等: 已经是 PAID 就跳过
        if ("PAID".equals(order.getStatus())) {
            return true;
        }

        // 乐观锁更新
        int rows = paymentOrderMapper.update(null,
                new LambdaUpdateWrapper<PaymentOrder>()
                        .eq(PaymentOrder::getOrderNo, orderNo)
                        .eq(PaymentOrder::getStatus, "PENDING")
                        .set(PaymentOrder::getStatus, "PAID")
                        .set(PaymentOrder::getPaidTime, LocalDateTime.now())
        );

        if (rows == 0) {
            log.warn("补单: 订单状态更新失败, orderNo={}", orderNo);
            return false;
        }

        fulfillOrder(order);
        log.info("补单完成, orderNo={}", orderNo);
        return true;
    }

    /**
     * 从 bizContent JSON 字符串中提取 planId
     * 格式示例: {"planId":1}
     */
    private Integer parsePlanIdFromBizContent(String bizContent) {
        if (bizContent == null || bizContent.isBlank()) {
            return 1; // 默认月卡
        }
        try {
            // 简单正则提取, 避免引入额外 JSON 库
            java.util.regex.Matcher matcher = java.util.regex.Pattern
                    .compile("\"planId\"\\s*:\\s*(\\d+)")
                    .matcher(bizContent);
            if (matcher.find()) {
                return Integer.parseInt(matcher.group(1));
            }
        } catch (Exception e) {
            log.warn("解析 bizContent 中的 planId 失败, bizContent={}", bizContent, e);
        }
        return 1;
    }
}

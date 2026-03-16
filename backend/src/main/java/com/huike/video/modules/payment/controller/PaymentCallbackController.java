package com.huike.video.modules.payment.controller;

import com.huike.video.modules.payment.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付回调控制器
 * 接收第三方支付平台的异步通知
 * 对应接口: POST /api/v1/callbacks/payment/{channel}
 *
 * 注意: 此接口为匿名访问 (无需登录Token)，需在 Sa-Token 排除列表中配置
 */
@Slf4j
@Tag(name = "支付回调", description = "接收第三方支付平台异步回调通知")
@RestController
@RequestMapping("/api/v1/callbacks/payment")
@RequiredArgsConstructor
public class PaymentCallbackController {

    private final OrderService orderService;

    /**
     * 支付宝异步回调通知
     * POST /api/v1/callbacks/payment/alipay
     */
    @Operation(summary = "支付宝异步回调", description = "接收支付宝异步通知，进行验签和业务处理")
    @PostMapping("/alipay")
    public String alipayNotify(HttpServletRequest request) {
        log.info("收到支付宝异步回调通知");

        // 1. 将 request 中的所有参数提取为 Map
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String[] values = entry.getValue();
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(entry.getKey(), valueStr.toString());
        }

        // 2. 交由 OrderService 处理
        boolean success = orderService.handleAlipayCallback(params);

        // 3. 按支付宝规范返回
        return success ? "success" : "fail";
    }
}

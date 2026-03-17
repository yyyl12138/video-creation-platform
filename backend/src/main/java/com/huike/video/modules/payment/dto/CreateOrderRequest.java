package com.huike.video.modules.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单请求 DTO
 * 对应接口: POST /api/v1/orders
 */
@Data
public class CreateOrderRequest {

    /**
     * 订单类型: RECHARGE, VIP_SUBSCRIPTION, TEMPLATE_PURCHASE
     */
    @NotBlank(message = "订单类型不能为空")
    private String orderType;

    /**
     * 订单金额
     */
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0.01")
    private BigDecimal amount;

    /**
     * 支付方式: ALIPAY, WECHAT, BALANCE
     */
    @NotBlank(message = "支付方式不能为空")
    private String payChannel;

    /**
     * 业务参数 (JSON 字符串)
     * 充值: {"pkgId": 1}
     * 会员: {"planId": 1}
     * 模版: {"templateId": "TMP_001"}
     */
    private String bizContent;
}

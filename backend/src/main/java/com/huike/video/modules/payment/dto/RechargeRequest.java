package com.huike.video.modules.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 发起充值请求 DTO
 * 对应接口: POST /api/v1/wallets/me/recharge
 * 本质是 CreateOrderRequest 的简化封装
 */
@Data
public class RechargeRequest {

    /**
     * 充值套餐ID (可选，传具体金额时可不传)
     */
    private Integer packageId;

    /**
     * 充值金额
     */
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01", message = "充值金额必须大于0.01")
    private BigDecimal amount;

    /**
     * 支付方式: ALIPAY, WECHAT
     */
    @NotBlank(message = "支付方式不能为空")
    private String payChannel;
}

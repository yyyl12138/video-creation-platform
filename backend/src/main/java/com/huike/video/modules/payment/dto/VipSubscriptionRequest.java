package com.huike.video.modules.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * VIP 订阅请求 DTO
 * 对应接口: POST /api/v1/users/me/vip-subscription
 */
@Data
public class VipSubscriptionRequest {

    /**
     * 会员套餐ID (月卡/年卡)
     */
    @NotNull(message = "套餐ID不能为空")
    private Integer planId;

    /**
     * 支付方式: ALIPAY, WECHAT
     */
    @NotBlank(message = "支付方式不能为空")
    private String payChannel;
}

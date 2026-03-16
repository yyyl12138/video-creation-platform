package com.huike.video.modules.payment.vo;

import lombok.Data;

/**
 * 创建订单响应 VO
 * 对应接口: POST /api/v1/orders
 */
@Data
public class CreateOrderResponse {

    /**
     * 系统订单号
     */
    private String orderId;

    /**
     * 支付链接 / 表单HTML (用于浏览器跳转)
     */
    private String payUrl;

    /**
     * 过期时间
     */
    private String expireTime;
}

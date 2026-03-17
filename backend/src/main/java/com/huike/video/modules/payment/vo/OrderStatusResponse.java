package com.huike.video.modules.payment.vo;

import lombok.Data;

/**
 * 订单状态查询响应 VO
 * 对应接口: GET /api/v1/orders/{orderId}
 */
@Data
public class OrderStatusResponse {

    /**
     * 系统订单号
     */
    private String orderId;

    /**
     * 订单状态: PENDING, PAID, CANCELLED, REFUNDED
     */
    private String status;

    /**
     * 支付时间 (仅 PAID 状态有值)
     */
    private String paidTime;
}

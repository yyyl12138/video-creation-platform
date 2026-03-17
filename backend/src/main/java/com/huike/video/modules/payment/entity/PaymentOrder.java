package com.huike.video.modules.payment.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_orders")
public class PaymentOrder extends BaseEntity {

    @TableId
    private String id;

    /**
     * 系统内订单号 (out_trade_no)
     */
    private String orderNo;

    /**
     * 所属用户ID
     */
    private String userId;

    /**
     * 订单类型: RECHARGE, VIP_SUBSCRIPTION, TEMPLATE_PURCHASE
     */
    private String orderType;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 支付渠道: ALIPAY, WECHAT, BALANCE
     */
    private String payChannel;

    /**
     * 订单状态: PENDING, PAID, CANCELLED, REFUNDED
     */
    private String status;

    /**
     * 第三方交易流水号 (支付宝 trade_no)
     */
    private String externalTradeNo;

    /**
     * 业务拓展参数 (JSON 字符串)
     */
    private String bizContent;

    /**
     * 订单标题/商品名称
     */
    private String subject;

    /**
     * 实际支付时间
     */
    private LocalDateTime paidTime;

    /**
     * 订单过期时间
     */
    private LocalDateTime expireTime;
}

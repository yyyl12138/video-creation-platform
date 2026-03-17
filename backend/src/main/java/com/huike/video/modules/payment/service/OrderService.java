package com.huike.video.modules.payment.service;

import com.huike.video.modules.payment.dto.CreateOrderRequest;
import com.huike.video.modules.payment.vo.CreateOrderResponse;
import com.huike.video.modules.payment.vo.OrderStatusResponse;

import java.util.Map;

/**
 * 订单服务接口
 * 负责订单的创建、查询、取消，以及回调处理
 */
public interface OrderService {

    /**
     * 创建支付订单
     * @param request 下单请求
     * @return 订单响应 (含支付链接)
     */
    CreateOrderResponse createOrder(CreateOrderRequest request);

    /**
     * 查询订单状态
     * @param orderId 系统订单号 (order_no)
     * @return 订单状态响应
     */
    OrderStatusResponse getOrderStatus(String orderId);

    /**
     * 取消订单
     * @param orderId 系统订单号 (order_no)
     * @return 是否成功
     */
    boolean cancelOrder(String orderId);

    /**
     * 处理支付宝异步回调
     * @param params 回调参数
     * @return 是否处理成功
     */
    boolean handleAlipayCallback(Map<String, String> params);

    /**
     * 内部补单（对账任务使用，跳过验签）
     * @param orderNo 订单号
     * @return 是否成功
     */
    boolean completeOrderByReconciliation(String orderNo);
}

package com.huike.video.modules.payment.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.payment.dto.CreateOrderRequest;
import com.huike.video.modules.payment.service.OrderService;
import com.huike.video.modules.payment.vo.CreateOrderResponse;
import com.huike.video.modules.payment.vo.OrderStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 * 对应接口文档 模块九 - 支付与订单中心
 */
@Tag(name = "订单中心", description = "订单创建、查询、取消")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建支付订单
     * POST /api/v1/orders
     */
    @Operation(summary = "创建支付订单", description = "根据订单类型和支付方式创建订单并获取支付链接")
    @PostMapping
    public Result<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return Result.success(orderService.createOrder(request));
    }

    /**
     * 查询订单状态
     * GET /api/v1/orders/{orderId}
     */
    @Operation(summary = "查询订单状态", description = "前端轮询订单支付状态")
    @GetMapping("/{orderId}")
    public Result<OrderStatusResponse> getOrderStatus(@PathVariable String orderId) {
        return Result.success(orderService.getOrderStatus(orderId));
    }

    /**
     * 取消订单
     * POST /api/v1/orders/{orderId}/cancel
     */
    @Operation(summary = "取消订单", description = "取消待支付的订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Boolean> cancelOrder(@PathVariable String orderId) {
        return Result.success(orderService.cancelOrder(orderId));
    }
}

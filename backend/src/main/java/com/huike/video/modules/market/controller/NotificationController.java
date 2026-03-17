package com.huike.video.modules.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.result.Result;
import com.huike.video.modules.market.entity.Notification;
import com.huike.video.modules.market.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "消息通知接口")
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@SaCheckLogin
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "获取消息列表")
    @GetMapping
    public Result<Page<Notification>> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String type) {
        return Result.success(notificationService.getMyNotifications(page, size, type));
    }

    @Operation(summary = "标记单条已读")
    @PutMapping("/{notificationId}/read")
    public Result<Boolean> markAsRead(@PathVariable Long notificationId) {
        return Result.success(notificationService.markAsRead(notificationId));
    }

    @Operation(summary = "全部标记已读")
    @PutMapping("/read-all")
    public Result<Boolean> markAllAsRead() {
        return Result.success(notificationService.markAllAsRead());
    }
}

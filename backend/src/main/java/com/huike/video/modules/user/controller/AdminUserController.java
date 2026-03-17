package com.huike.video.modules.user.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.result.Result;
import com.huike.video.modules.user.dto.UserStatusUpdateRequest;
import com.huike.video.modules.user.service.UserService;
import com.huike.video.modules.user.vo.AdminUserPageVO;
import com.huike.video.modules.user.vo.UserMeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台 - 用户管理接口")
@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@SaCheckRole("ADMIN")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "多条件分页查询用户列表")
    @GetMapping
    public Result<Page<AdminUserPageVO>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        return Result.success(userService.getAdminUserPage(page, size, keyword, status));
    }

    @Operation(summary = "获取用户详情")
    @GetMapping("/{userId}")
    public Result<UserMeResponse> getUserDetail(@PathVariable String userId) {
        return Result.success(userService.getUserDetailForAdmin(userId));
    }

    @Operation(summary = "封禁/解封用户")
    @PutMapping("/{userId}/status")
    public Result<Boolean> updateUserStatus(
            @PathVariable String userId,
            @Valid @RequestBody UserStatusUpdateRequest request) {
        return Result.success(userService.updateUserStatus(userId, request.getStatus(), request.getReason()));
    }
}

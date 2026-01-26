package com.huike.video.modules.auth.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.auth.dto.AuthLoginRequest;
import com.huike.video.modules.auth.dto.AuthRegisterRequest;
import com.huike.video.modules.auth.dto.AuthSwitchRoleRequest;
import com.huike.video.modules.auth.service.AuthService;
import com.huike.video.modules.auth.vo.AuthLoginResponse;
import com.huike.video.modules.auth.vo.AuthRegisterResponse;
import com.huike.video.modules.auth.vo.AuthSwitchRoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<AuthRegisterResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/login")
    public Result<AuthLoginResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/switch-role")
    public Result<AuthSwitchRoleResponse> switchRole(@Valid @RequestBody AuthSwitchRoleRequest request) {
        return Result.success(authService.switchRole(request));
    }
}

package com.huike.video.modules.auth.controller;

import com.huike.video.common.result.Result;
import com.huike.video.modules.auth.dto.AuthLoginRequest;
import com.huike.video.modules.auth.dto.AuthRegisterRequest;
import com.huike.video.modules.auth.dto.AuthSwitchRoleRequest;
import com.huike.video.modules.auth.dto.LoginBySmsRequest;
import com.huike.video.modules.auth.dto.RefreshTokenRequest;
import com.huike.video.modules.auth.dto.ResetPasswordRequest;
import com.huike.video.modules.auth.dto.SendCodeRequest;
import com.huike.video.modules.auth.service.AuthService;
import com.huike.video.modules.auth.vo.AuthLoginResponse;
import com.huike.video.modules.auth.vo.AuthRefreshResponse;
import com.huike.video.modules.auth.vo.AuthRegisterResponse;
import com.huike.video.modules.auth.vo.AuthSwitchRoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    public Map<String, Object> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthLoginResponse data = authService.login(request);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 20000);
        result.put("message", "success");
        result.put("data", data);
        result.put("timestamp", System.currentTimeMillis());
        result.put("token", data.getToken());
        result.put("refreshToken", data.getRefreshToken());
        result.put("expireIn", data.getExpireIn());
        result.put("userInfo", data.getUserInfo());
        return result;
    }

    @PostMapping("/send-code")
    public Result<Boolean> sendCode(@Valid @RequestBody SendCodeRequest request) {
        return Result.success(authService.sendCode(request));
    }

    @PostMapping("/login-by-sms")
    public Result<AuthLoginResponse> loginBySms(@Valid @RequestBody LoginBySmsRequest request) {
        return Result.success(authService.loginBySms(request));
    }

    @PostMapping("/reset-password")
    public Result<Boolean> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return Result.success(authService.resetPassword(request));
    }

    @PostMapping("/refresh")
    public Result<AuthRefreshResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return Result.success(authService.refresh(request));
    }

    @PostMapping("/logout")
    public Result<Boolean> logout() {
        return Result.success(authService.logout());
    }

    @PostMapping("/switch-role")
    public Result<AuthSwitchRoleResponse> switchRole(@Valid @RequestBody AuthSwitchRoleRequest request) {
        return Result.success(authService.switchRole(request));
    }
}

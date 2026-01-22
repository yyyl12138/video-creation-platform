package com.huike.video.modules.auth.service;

import com.huike.video.modules.auth.dto.AuthLoginRequest;
import com.huike.video.modules.auth.dto.AuthRegisterRequest;
import com.huike.video.modules.auth.dto.AuthSwitchRoleRequest;
import com.huike.video.modules.auth.vo.AuthLoginResponse;
import com.huike.video.modules.auth.vo.AuthRegisterResponse;
import com.huike.video.modules.auth.vo.AuthSwitchRoleResponse;

public interface AuthService {

    AuthRegisterResponse register(AuthRegisterRequest request);

    AuthLoginResponse login(AuthLoginRequest request);

    AuthSwitchRoleResponse switchRole(AuthSwitchRoleRequest request);
}

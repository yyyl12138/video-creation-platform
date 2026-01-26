package com.huike.video.modules.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AuthSwitchRoleRequest {

    @NotNull(message = "roleId不能为空")
    private Long roleId;
}

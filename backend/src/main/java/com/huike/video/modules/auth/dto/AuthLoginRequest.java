package com.huike.video.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLoginRequest {

    @NotBlank(message = "username不能为空")
    private String username;

    @NotBlank(message = "password不能为空")
    private String password;

    private Long roleId;
}

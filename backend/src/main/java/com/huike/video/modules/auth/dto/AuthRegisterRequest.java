package com.huike.video.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRegisterRequest {

    @NotBlank(message = "username不能为空")
    @Size(min = 4, max = 20, message = "username长度必须在4-20之间")
    private String username;

    @NotBlank(message = "email不能为空")
    @Email(message = "email格式不正确")
    private String email;

    @NotBlank(message = "password不能为空")
    @Size(min = 6, max = 20, message = "password长度必须在6-20之间")
    private String password;

    private Long roleId;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "phone格式不正确")
    private String phone;
}

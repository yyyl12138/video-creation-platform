package com.huike.video.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发送验证码请求
 */
@Data
public class SendCodeRequest {

    @NotBlank(message = "手机号或邮箱不能为空")
    private String target;

    /** 验证码类型: REGISTER/RESET_PWD */
    private String type;
}

package com.huike.video.modules.user.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateRequest {
    
    @NotNull(message = "状态标识不能为空")
    @Min(value = 1, message = "状态值不合法")
    @Max(value = 2, message = "只能设为1(正常)或2(封禁)")
    private Integer status;
    
    private String reason;
}

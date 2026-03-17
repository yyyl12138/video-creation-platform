package com.huike.video.modules.sys.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

@Data
public class UpdateSysConfigRequest {
    
    @NotNull(message = "配置项不能为空")
    private Map<String, String> configs;
}

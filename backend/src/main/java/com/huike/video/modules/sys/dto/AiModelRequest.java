package com.huike.video.modules.sys.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class AiModelRequest {

    private Long modelId;

    @NotBlank(message = "模型名称不能为空")
    private String modelName;

    @NotBlank(message = "模型标识不能为空")
    private String modelKey;

    @NotBlank(message = "提供商不能为空")
    private String provider;

    @NotBlank(message = "类型不能为空")
    private String modelType;

    @NotBlank(message = "接口地址不能为空")
    private String apiEndpoint;

    @NotNull(message = "API配置不能为空")
    private Map<String, Object> apiConfig;

    @NotNull(message = "计费模式不能为空")
    private Integer billingMode;

    private BigDecimal unitPrice;

    private Boolean isActive;
}

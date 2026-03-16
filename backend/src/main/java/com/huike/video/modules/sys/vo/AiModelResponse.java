package com.huike.video.modules.sys.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class AiModelResponse {

    private Long id;
    private Long modelId; // 为保持前台一致性, 提供 alias 或者将 id 赋给它
    private String modelName;
    private String modelKey;
    private String provider;
    private String modelType;
    private String apiEndpoint;
    private Map<String, Object> apiConfig;
    private Integer billingMode;
    private BigDecimal unitPrice;
    private Boolean isActive;

}

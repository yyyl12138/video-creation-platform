package com.huike.video.modules.creation.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 统一创作请求参数
 */
@Data
public class CreationRequest implements Serializable {
    /**
     * 任务类型: TEXT_TO_IMAGE, TEXT_TO_VIDEO, IMAGE_TO_VIDEO 等
     */
    private String taskType;

    /**
     * 前端推荐使用此字段，后端自动映射为 modelKey
     */
    private String modelName;

    /**
     * 模型标识 (精确标识，如 "wanx-v1")
     * 可选，如果同时提供 modelName 和 modelKey，优先使用 modelKey
     */
    private String modelKey;

    /**
     * 使用的模板ID (可选)
     */
    private String templateId;

    /**
     * 核心参数配置 (包含 prompt, negativePrompt, ratio, duration 等)
     */
    @NotNull(message = "inputConfig 不能为空")
    private Map<String, Object> inputConfig;

    /**
     * 任务优先级 (默认5)
     */
    private Integer priority;

    // Compatibility methods for existing strategies
    public String getPrompt() {
        return inputConfig != null ? (String) inputConfig.get("prompt") : null;
    }

    public String getNegativePrompt() {
        return inputConfig != null ? (String) inputConfig.get("negativePrompt") : null;
    }

    public Map<String, Object> getExtraMap() {
        return inputConfig;
    }
}


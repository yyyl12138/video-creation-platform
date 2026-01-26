package com.huike.video.modules.creation.domain.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Map;

/**
 * 统一创作请求参数
 */
@Data
public class CreationRequest implements Serializable {
    /**
     * 任务类型: TEXT_TO_TEXT, TEXT_TO_IMAGE, TEXT_TO_VIDEO, etc.
     */
    private String taskType;

    /**
     * 提示词
     */
    private String prompt;

    /**
     * 负向提示词
     */
    private String negativePrompt;

    /**
     * 指定模型Key (可选)
     */
    private String modelKey;

    /**
     * 其它参数 (宽高比、风格、时长等)
     */
    private Map<String, Object> extraMap;
}

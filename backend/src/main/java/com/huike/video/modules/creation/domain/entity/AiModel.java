package com.huike.video.modules.creation.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Map;

/**
 * AI 模型配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ai_models", autoResultMap = true)
public class AiModel extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 模型标识 (如 deepseek-v3)
     */
    private String modelKey;

    /**
     * 提供商 (DEEPSEEK, ALIYUN, KUAISHOU...)
     */
    private String provider;

    /**
     * 类型: TEXT, IMAGE, VIDEO
     */
    private String modelType;

    /**
     * 接口地址
     */
    private String apiEndpoint;

    /**
     * API配置 (JSON存储)
     *包含 apiKey, secretKey 等
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> apiConfig;

    /**
     * 计费: 1-按次, 2-Token
     */
    private Integer billingMode;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 逻辑删除标识
     */
    private Boolean isDeleted;
}

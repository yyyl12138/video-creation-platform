package com.huike.video.modules.creation.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI 生成任务实体
 * 对应表: ai_generation_tasks
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "ai_generation_tasks", autoResultMap = true)
public class AiTask extends BaseEntity {

    @TableId
    private String id;

    private String userId;

    /**
     * 任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-图生视频
     */
    private Integer taskType;

    private String templateId;

    /**
     * 用户提示词
     */
    private String prompt;

    /**
     * 负面提示词
     */
    private String negativePrompt;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> inputConfig;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> outputConfig;

    /**
     * 状态: 1-等待中, 2-处理中, 3-成功, 4-失败, 5-取消
     */
    private Integer status;


    private LocalDateTime endTime;

    /**
     * 结果文件路径 (图片/视频)
     */
    private String resultFilePath;

    /**
     * 结果封面图路径
     */
    private String resultCoverPath;

    /**
     * 结果文件大小
     */
    private Long resultFileSize;

    private String errorMessage;

    private Integer retryCount;

    private Integer priority;

    private Long modelId;

    /**
     * 第三方任务 ID (如阿里云、快手等)
     */
    private String externalTaskId;

    /**
     * 消耗的 Token/费用
     */
    private BigDecimal costToken;

    /**
     * 逻辑删除标识
     */
    @com.baomidou.mybatisplus.annotation.TableLogic
    private Integer isDeleted;
}

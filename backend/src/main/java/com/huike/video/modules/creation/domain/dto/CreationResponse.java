package com.huike.video.modules.creation.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.io.Serializable;

/**
 * 统一创作响应结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreationResponse implements Serializable {
    /**
     * 任务ID (异步任务返回)
     */
    private String taskId;

    /**
     * 生成内容 (同步任务返回文本结果)
     */
    private String content;

    /**
     * 媒体URL (同步返回图片/视频地址时使用)
     */
    private String mediaUrl;
    
    /**
     * 消耗的 token 数 (估算)
     */
    private Integer usageTokens;

    /**
     * 任务状态 (SUCCESS, PROCESSING, FAILED)
     */
    private String status;

    /**
     * 封面图 URL
     */
    private String coverUrl;

    /**
     * 错误信息
     */
    private String errorMessage;
}

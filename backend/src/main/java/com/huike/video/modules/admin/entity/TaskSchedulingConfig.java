package com.huike.video.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务调度配置表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_scheduling_configs")
public class TaskSchedulingConfig extends BaseEntity {

    @TableId
    private Long id;

    /** 并发转码数 */
    private Integer maxConcurrentTranscoding;

    /** 并发生成数 */
    private Integer maxConcurrentGeneration;

    /** 队列限制 */
    private Integer taskQueueLimit;

    /** CPU阈值 */
    private Integer cpuThresholdPercent;

    /** 内存阈值 */
    private Integer memoryThresholdPercent;

    /** 重试策略 (JSON) */
    private String retryPolicy;

    /** 超时策略 (JSON) */
    private String timeoutPolicy;

    /** 调度算法 */
    private String scheduleAlgorithm;

    /** 描述 */
    private String description;

    @TableLogic
    private Integer isDeleted;
}

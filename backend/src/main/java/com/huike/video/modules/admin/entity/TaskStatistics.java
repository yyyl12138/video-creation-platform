package com.huike.video.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 任务统计表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("task_statistics")
public class TaskStatistics extends BaseEntity {

    @TableId
    private Long id;

    /** 任务类型: 1-文生文, 2-文生图, 3-文生视频, 4-文加图生视频 */
    private Integer taskType;

    /** 统计日期 */
    private LocalDate statDate;

    /** 总任务数 */
    private Integer totalCount;

    /** 成功数 */
    private Integer successCount;

    /** 失败数 */
    private Integer failCount;

    /** 平均耗时 */
    private Integer avgDurationSeconds;

    /** 峰值时段 */
    private Integer peakHour;

    @TableLogic
    private Integer isDeleted;
}

package com.huike.video.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 内容审核表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("content_reviews")
public class ContentReview extends BaseEntity {

    @TableId
    private Long id;

    /** 内容ID(UGC表ID) */
    private String contentId;

    /** 审核员ID(User表ID) */
    private String reviewerId;

    /** 状态: 1-待审核, 2-通过, 3-驳回, 4-复审 */
    private Integer reviewStatus;

    /** 审核时间 */
    private LocalDateTime reviewTime;

    /** 驳回原因 */
    private String rejectReason;

    /** 建议 */
    private String suggestions;

    /** 下次审核时间 */
    private LocalDateTime nextReviewTime;

    /** 版本号 */
    private Integer version;

    @TableLogic
    private Integer isDeleted;
}

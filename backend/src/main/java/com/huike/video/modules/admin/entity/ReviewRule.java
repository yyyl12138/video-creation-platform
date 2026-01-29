package com.huike.video.modules.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审核规则表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("review_rules")
public class ReviewRule extends BaseEntity {

    @TableId
    private Long id;

    /** 规则名称 */
    private String ruleName;

    /** 规则类型 */
    private String ruleType;

    /** 审核条件 (JSON) */
    private String conditions;

    /** 审核动作 (JSON) */
    private String actions;

    /** 优先级 */
    private Integer priority;

    /** 状态: 1-启用, 2-禁用 */
    private Integer status;

    /** 创建者ID */
    private String creatorId;

    /** 描述 */
    private String description;

    @TableLogic
    private Integer isDeleted;
}

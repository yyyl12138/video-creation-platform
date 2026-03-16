package com.huike.video.modules.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户互动记录（点赞/关注）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_interactions")
public class UserInteraction extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 操作者ID */
    private String userId;

    /** 目标ID (模版ID或被关注人ID) */
    private String targetId;

    /** 互动类型: LIKE_TEMPLATE, FOLLOW_USER */
    private String actionType;
}

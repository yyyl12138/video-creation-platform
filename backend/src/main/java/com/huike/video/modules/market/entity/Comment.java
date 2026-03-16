package com.huike.video.modules.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 模版评论表（两级嵌套）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("comments")
public class Comment extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 评论者ID */
    private String userId;

    /** 目标模版ID */
    private String targetId;

    /** 父评论ID, 顶级为0 */
    private Long parentId;

    /** 评论内容 */
    private String content;

    /** 点赞数 */
    private Integer likeCount;

    @TableLogic
    private Integer isDeleted;
}

package com.huike.video.modules.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 消息通知实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("notifications")
public class Notification extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接收者用户ID */
    private String userId;

    /** 通知类型: SYSTEM, LIKE, FOLLOW, COMMENT, PURCHASE */
    private String type;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 关联的业务ID (如模版ID、评论ID等) */
    private String relatedId;

    /** 是否已读: 0-未读, 1-已读 */
    private Integer isRead;

    @TableLogic
    private Integer isDeleted;
}

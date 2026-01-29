package com.huike.video.modules.community.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户生成内容表 (UGC)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_generated_content")
public class UserGeneratedContent extends BaseEntity {

    @TableId
    private String id;

    /** 创作者ID(creators表ID) */
    private String creatorId;

    /** 内容名称 */
    private String contentName;

    /** 关联任务ID */
    private String taskId;

    /** 格式 */
    private String format;

    /** 分辨率 */
    private String resolution;

    /** 文件路径 */
    private String filePath;

    /** 文件大小 */
    private Long fileSize;

    /** 缩略图 */
    private String thumbnailPath;

    /** 描述 */
    private String description;

    /** 标签 */
    private String tags;

    @TableLogic
    private Integer isDeleted;
}

package com.huike.video.modules.community.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 视频模板表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_templates")
public class VideoTemplate extends BaseEntity {

    @TableId
    private String id;

    /** 模板名称 */
    private String templateName;

    /** 类型 */
    private String type;

    /** 宽高比 */
    private String aspectRatio;

    /** 预览图路径 */
    private String previewImagePath;

    /** 模板文件路径 */
    private String templateFilePath;

    /** 创建者ID */
    private String creatorId;

    /** 使用次数 */
    private Integer usageCount;

    /** 状态: 1-启用, 2-禁用, 3-草稿 */
    private Integer status;

    /** 标签 */
    private String tags;

    /** 描述 */
    private String description;

    /** 模板配置 (JSON) */
    private String configJson;

    @TableLogic
    private Integer isDeleted;
}

package com.huike.video.modules.material.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片素材实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("image_materials")
public class ImageMaterial extends BaseEntity {

    @TableId
    private String id;

    /** 图片名称 */
    private String imageName;

    /** 分类 */
    private String category;

    /** 文件路径 */
    private String filePath;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 分辨率 */
    private String resolution;

    /** 格式 */
    private String format;

    /** 上传者ID */
    private String uploaderId;

    /** 状态: 1-正常, 2-封禁, 3-禁用 */
    private Integer status;

    /** 版权: 1-免费商用, 2-付费授权, 3-个人使用 */
    private Integer copyrightStatus;

    /** 标签 */
    private String tags;

    /** 描述 */
    private String description;

    /** 来源: 1-官方, 2-用户上传, 3-AI生成 */
    private Integer sourceType;

    /** 是否公开 */
    private Boolean isPublic;

    @TableLogic
    private Integer isDeleted;
}

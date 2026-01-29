package com.huike.video.modules.material.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("video_materials")
public class VideoMaterial extends BaseEntity {

    @TableId
    private String id;

    private String videoName;

    private String type;

    private String resolution;

    private String format;

    private BigDecimal durationSeconds;

    private String filePath;

    private Long fileSize;

    private String uploaderId;

    private Integer status; // 1-正常, 2-封禁, 3-禁用

    private Integer copyrightStatus; // 1-免费商用, 2-付费授权, 3-个人使用

    private String tags;

    private String description;

    private Integer sourceType; // 1-官方, 2-用户上传, 3-AI生成

    private Boolean isPublic;

    private Integer isDeleted; // 逻辑删除标识
}

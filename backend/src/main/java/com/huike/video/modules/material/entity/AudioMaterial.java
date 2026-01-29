package com.huike.video.modules.material.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("audio_materials")
public class AudioMaterial extends BaseEntity {

    @TableId
    private String id;

    private String audioName;

    private String type;

    private BigDecimal durationSeconds;

    private Long fileSize;

    private String filePath;

    private Integer bitrate;

    private String sampleRate;

    private String uploaderId;

    private Integer status; // 1-正常, 2-封禁, 3-禁用

    private Integer copyrightStatus; // 1-免费商用, 2-付费授权, 3-个人使用

    private String tags;

    private String description;

    private Integer isDeleted; // 逻辑删除标识
}

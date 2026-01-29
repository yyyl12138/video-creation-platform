package com.huike.video.modules.material.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.huike.video.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 音频素材实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("audio_materials")
public class AudioMaterial extends BaseEntity {

    @TableId
    private String id;

    /** 音频名称 */
    private String audioName;

    /** 类型 */
    private String type;

    /** 时长(秒) */
    private BigDecimal durationSeconds;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件路径 */
    private String filePath;

    /** 比特率 */
    private Integer bitrate;

    /** 采样率 */
    private String sampleRate;

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

    @TableLogic
    private Integer isDeleted;
}

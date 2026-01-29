package com.huike.video.modules.material.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MaterialVO {
    private String id;
    private String name;
    private String type; // image/video/audio
    private String url;
    private Long size;
    private LocalDateTime createTime;
    private String resolution; // 视频/图片分辨率
    private BigDecimal durationSeconds; // 视频/音频时长
    private String format; // 文件格式
}

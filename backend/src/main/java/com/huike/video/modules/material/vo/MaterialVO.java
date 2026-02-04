package com.huike.video.modules.material.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 素材统一响应 VO
 * 符合接口文档规范 3.1
 */
@Data
public class MaterialVO {

    /** 素材ID */
    private String materialId;

    /** 素材名称 */
    private String name;

    /** 素材类型: IMAGE/VIDEO/AUDIO */
    private String type;

    /** 访问URL */
    private String url;

    /** 封面URL (视频专属) */
    private String coverUrl;

    /** 分辨率 (图片/视频) */
    private String resolution;

    /** 时长秒数 (视频/音频) */
    private BigDecimal durationSeconds;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 标签列表 */
    private List<String> tags;

    /** 来源类型: SYSTEM(官方)/USER_UPLOAD(用户上传)/AI_GENERATED(AI生成) */
    private String sourceType;

    /** 版权状态: FREE_COMMERCIAL(免费商用)/PAID(付费授权)/PERSONAL_USE(个人使用) */
    private String copyrightStatus;

    /** 是否系统素材 */
    private Boolean isSystem;

    /** 创建时间 */
    private String createTime;
}

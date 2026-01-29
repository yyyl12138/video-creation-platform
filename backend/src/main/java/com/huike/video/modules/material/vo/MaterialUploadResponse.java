package com.huike.video.modules.material.vo;

import lombok.Data;

/**
 * 素材上传响应 VO
 */
@Data
public class MaterialUploadResponse {

    /** 素材ID */
    private String materialId;

    /** 访问URL */
    private String url;

    /** 素材类型 */
    private String type;

    /** 文件大小(字节) */
    private Long fileSize;
}

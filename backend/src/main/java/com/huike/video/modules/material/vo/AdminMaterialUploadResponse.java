package com.huike.video.modules.material.vo;

import lombok.Data;

/**
 * 管理员素材上传响应 VO
 * 对应接口 1.1
 */
@Data
public class AdminMaterialUploadResponse {

    /** 素材ID */
    private String materialId;

    /** 访问URL */
    private String url;

    /** 版权状态 */
    private String copyrightStatus;
}

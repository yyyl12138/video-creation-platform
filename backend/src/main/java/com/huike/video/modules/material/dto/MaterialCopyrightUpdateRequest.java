package com.huike.video.modules.material.dto;

import lombok.Data;

/**
 * 素材版权更新请求 DTO
 * 对应接口 1.3
 */
@Data
public class MaterialCopyrightUpdateRequest {

    /** 素材类型: IMAGE/VIDEO/AUDIO */
    private String type;

    /** 版权状态: FREE_COMMERCIAL(免费商用), PAID(付费授权), PERSONAL_USE(个人使用) */
    private String copyrightStatus;
}

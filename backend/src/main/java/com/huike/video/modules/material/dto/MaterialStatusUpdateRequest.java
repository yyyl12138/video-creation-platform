package com.huike.video.modules.material.dto;

import lombok.Data;

/**
 * 素材状态更新请求 DTO
 * 对应接口 1.2
 */
@Data
public class MaterialStatusUpdateRequest {

    /** 素材类型: IMAGE/VIDEO/AUDIO */
    private String type;

    /** 目标状态: NORMAL(正常), BANNED(封禁), REVIEWING(审核中) */
    private String status;

    /** 操作原因 (封禁时必填) */
    private String reason;
}

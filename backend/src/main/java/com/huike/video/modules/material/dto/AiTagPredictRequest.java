package com.huike.video.modules.material.dto;

import lombok.Data;

/**
 * AI 标签识别请求 DTO
 * 对应接口 2.2
 */
@Data
public class AiTagPredictRequest {

    /** 素材地址 */
    private String materialUrl;

    /** 素材类型: IMAGE/VIDEO */
    private String type;
}

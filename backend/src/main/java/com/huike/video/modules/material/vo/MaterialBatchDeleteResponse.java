package com.huike.video.modules.material.vo;

import lombok.Data;

/**
 * 批量删除素材响应 VO
 */
@Data
public class MaterialBatchDeleteResponse {

    /** 成功数量 */
    private Integer successCount;

    /** 失败数量 */
    private Integer failCount;
}

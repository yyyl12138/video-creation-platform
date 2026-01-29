package com.huike.video.modules.material.vo;

import lombok.Data;
import java.util.List;

/**
 * 批量删除素材请求 DTO
 */
@Data
public class MaterialBatchDeleteRequest {

    /** 素材ID列表 */
    private List<String> materialIds;

    /** 素材类型: IMAGE/VIDEO/AUDIO */
    private String type;
}

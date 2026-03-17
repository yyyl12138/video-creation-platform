package com.huike.video.modules.market.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 互动请求 (点赞/关注)
 */
@Data
public class InteractionRequest {

    @NotBlank(message = "目标ID不能为空")
    private String targetId;
}

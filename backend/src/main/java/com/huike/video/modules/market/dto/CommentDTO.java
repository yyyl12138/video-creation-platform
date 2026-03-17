package com.huike.video.modules.market.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 评论发表请求
 */
@Data
public class CommentDTO {

    /** 模版ID */
    @NotBlank(message = "模版ID不能为空")
    private String targetId;

    /** 父评论ID, 顶级为0 */
    private Long parentId;

    /** 内容 */
    @NotBlank(message = "评论内容不能为空")
    private String content;
}

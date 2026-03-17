package com.huike.video.modules.market.vo;

import lombok.Data;
import java.util.List;

/**
 * 评论展示 VO (两级嵌套)
 */
@Data
public class CommentVO {
    private Long commentId;
    private String userId;
    private String nickname;
    private String avatar;
    private String content;
    private Integer likeCount;
    private String createdAt;
    /** 仅一级评论才有子回复 */
    private List<CommentVO> replies;
}

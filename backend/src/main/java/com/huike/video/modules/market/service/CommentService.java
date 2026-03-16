package com.huike.video.modules.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.market.dto.CommentDTO;
import com.huike.video.modules.market.vo.CommentVO;

public interface CommentService {

    /**
     * 分页获取模版评论 (含子回复)
     */
    Page<CommentVO> getComments(String targetId, int page, int size);

    /**
     * 发表评论
     */
    Long addComment(CommentDTO dto);

    /**
     * 删除评论 (仅本人或管理员)
     */
    Boolean deleteComment(Long commentId);
}

package com.huike.video.modules.market.service;

public interface SocialInteractionService {

    /**
     * 点赞/取消点赞模版 (幂等)
     * @return true=点赞成功, false=取消成功
     */
    boolean toggleLike(String templateId);

    /**
     * 关注/取关作者 (幂等)
     * @return true=关注成功, false=取关成功
     */
    boolean toggleFollow(String authorId);
}

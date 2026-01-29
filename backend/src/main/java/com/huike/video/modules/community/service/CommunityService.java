package com.huike.video.modules.community.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.community.entity.VideoTemplate;
import com.huike.video.modules.community.entity.UserGeneratedContent;

import java.util.List;

/**
 * 社区/模版市场服务接口
 */
public interface CommunityService {

    /**
     * 获取模版市场列表
     */
    Page<VideoTemplate> listTemplates(Integer page, Integer size, String sort, String keyword);

    /**
     * 获取模版详情
     */
    VideoTemplate getTemplateById(String templateId);

    /**
     * 购买/获取模版
     */
    boolean purchaseTemplate(String userId, String templateId);

    /**
     * 获取用户生成内容列表
     */
    Page<UserGeneratedContent> listUserContent(String creatorId, Integer page, Integer size);

    /**
     * 关注/取关创作者
     */
    boolean followCreator(String userId, String authorId, boolean action);
}

package com.huike.video.modules.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.community.entity.VideoTemplate;
import com.huike.video.modules.community.entity.UserGeneratedContent;
import com.huike.video.modules.community.mapper.VideoTemplateMapper;
import com.huike.video.modules.community.mapper.UserGeneratedContentMapper;
import com.huike.video.modules.community.service.CommunityService;
import com.huike.video.modules.user.entity.Creator;
import com.huike.video.modules.user.mapper.CreatorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 社区/模版市场服务实现
 * 注入 user 模块的 CreatorMapper 以复用创作者数据
 */
@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    // ========== 本模块依赖 ==========
    private final VideoTemplateMapper videoTemplateMapper;
    private final UserGeneratedContentMapper userGeneratedContentMapper;

    // ========== user 模块依赖 ==========
    private final CreatorMapper creatorMapper;

    @Override
    public Page<VideoTemplate> listTemplates(Integer page, Integer size, String sort, String keyword) {
        Page<VideoTemplate> pageParam = new Page<>(page != null ? page : 1, Math.min(size != null ? size : 20, 100));
        
        LambdaQueryWrapper<VideoTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(VideoTemplate::getStatus, 1); // 仅启用状态
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(VideoTemplate::getTemplateName, keyword)
                   .or().like(VideoTemplate::getTags, keyword);
        }
        
        // 排序处理
        if ("NEW".equalsIgnoreCase(sort)) {
            wrapper.orderByDesc(VideoTemplate::getCreatedAt);
        } else if ("PRICE_ASC".equalsIgnoreCase(sort)) {
            // TODO: 需要额外的价格字段
            wrapper.orderByDesc(VideoTemplate::getUsageCount);
        } else {
            // 默认按热度(使用次数)排序
            wrapper.orderByDesc(VideoTemplate::getUsageCount);
        }
        
        return videoTemplateMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public VideoTemplate getTemplateById(String templateId) {
        return videoTemplateMapper.selectById(templateId);
    }

    @Override
    public boolean purchaseTemplate(String userId, String templateId) {
        // TODO: 实现购买逻辑 - 扣费 + 添加到用户资产
        return true;
    }

    @Override
    public Page<UserGeneratedContent> listUserContent(String creatorId, Integer page, Integer size) {
        Page<UserGeneratedContent> pageParam = new Page<>(page != null ? page : 1, Math.min(size != null ? size : 20, 100));
        
        LambdaQueryWrapper<UserGeneratedContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserGeneratedContent::getCreatorId, creatorId)
               .orderByDesc(UserGeneratedContent::getCreatedAt);
        
        return userGeneratedContentMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public boolean followCreator(String userId, String authorId, boolean action) {
        // 更新创作者粉丝数
        Creator creator = creatorMapper.selectById(authorId);
        if (creator == null) {
            return false;
        }
        
        int delta = action ? 1 : -1;
        creator.setFollowerCount(Math.max(0, creator.getFollowerCount() + delta));
        creatorMapper.updateById(creator);
        
        // TODO: 记录关注关系到单独的关注表
        return true;
    }
}

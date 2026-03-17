package com.huike.video.modules.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huike.video.modules.community.entity.VideoTemplate;
import com.huike.video.modules.community.mapper.VideoTemplateMapper;
import com.huike.video.modules.market.entity.UserInteraction;
import com.huike.video.modules.market.mapper.UserInteractionMapper;
import com.huike.video.modules.market.service.SocialInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialInteractionServiceImpl implements SocialInteractionService {

    private final UserInteractionMapper interactionMapper;
    private final VideoTemplateMapper videoTemplateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleLike(String templateId) {
        String userId = StpUtil.getLoginIdAsString();

        // 查询是否已点赞
        UserInteraction existing = interactionMapper.selectOne(new LambdaQueryWrapper<UserInteraction>()
                .eq(UserInteraction::getUserId, userId)
                .eq(UserInteraction::getTargetId, templateId)
                .eq(UserInteraction::getActionType, "LIKE_TEMPLATE"));

        if (existing != null) {
            // 取消点赞
            interactionMapper.deleteById(existing.getId());
            // likeCount - 1
            videoTemplateMapper.update(null, new LambdaUpdateWrapper<VideoTemplate>()
                    .eq(VideoTemplate::getId, templateId)
                    .gt(VideoTemplate::getLikeCount, 0)
                    .setSql("like_count = like_count - 1"));
            return false; // false = 取消
        } else {
            // 新增点赞
            UserInteraction interaction = new UserInteraction();
            interaction.setUserId(userId);
            interaction.setTargetId(templateId);
            interaction.setActionType("LIKE_TEMPLATE");
            interactionMapper.insert(interaction);
            // likeCount + 1
            videoTemplateMapper.update(null, new LambdaUpdateWrapper<VideoTemplate>()
                    .eq(VideoTemplate::getId, templateId)
                    .setSql("like_count = like_count + 1"));
            return true; // true = 已点赞
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleFollow(String authorId) {
        String userId = StpUtil.getLoginIdAsString();

        UserInteraction existing = interactionMapper.selectOne(new LambdaQueryWrapper<UserInteraction>()
                .eq(UserInteraction::getUserId, userId)
                .eq(UserInteraction::getTargetId, authorId)
                .eq(UserInteraction::getActionType, "FOLLOW_USER"));

        if (existing != null) {
            interactionMapper.deleteById(existing.getId());
            return false; // 取关
        } else {
            UserInteraction interaction = new UserInteraction();
            interaction.setUserId(userId);
            interaction.setTargetId(authorId);
            interaction.setActionType("FOLLOW_USER");
            interactionMapper.insert(interaction);
            return true; // 关注
        }
    }
}

package com.huike.video.modules.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.common.exception.BusinessException;
import com.huike.video.modules.community.entity.VideoTemplate;
import com.huike.video.modules.community.mapper.VideoTemplateMapper;
import com.huike.video.modules.market.entity.UserInteraction;
import com.huike.video.modules.market.entity.UserPurchasedTemplate;
import com.huike.video.modules.market.mapper.UserInteractionMapper;
import com.huike.video.modules.market.mapper.UserPurchasedTemplateMapper;
import com.huike.video.modules.market.service.MarketService;
import com.huike.video.modules.market.vo.TemplateMarketVO;
import com.huike.video.modules.user.entity.User;
import com.huike.video.modules.user.mapper.UserMapper;
import com.huike.video.modules.user.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {

    private final VideoTemplateMapper videoTemplateMapper;
    private final UserPurchasedTemplateMapper purchasedMapper;
    private final UserInteractionMapper interactionMapper;
    private final UserMapper userMapper;
    private final WalletService walletService;

    @Override
    public Page<TemplateMarketVO> getMarketTemplates(int page, int size, String type, String keyword) {
        Page<VideoTemplate> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<VideoTemplate> wrapper = new LambdaQueryWrapper<>();
        // 仅展示已启用的模版
        wrapper.eq(VideoTemplate::getStatus, 1);
        wrapper.eq(StringUtils.hasText(type), VideoTemplate::getType, type);
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(VideoTemplate::getTemplateName, keyword)
                    .or().like(VideoTemplate::getTags, keyword));
        }
        wrapper.orderByDesc(VideoTemplate::getUsageCount);

        Page<VideoTemplate> tplPage = videoTemplateMapper.selectPage(pageParam, wrapper);

        // 如果用户已登录, 批量查询购买和点赞状态
        String currentUserId = null;
        try { currentUserId = StpUtil.getLoginIdAsString(); } catch (Exception ignored) {}

        final String userId = currentUserId;

        List<TemplateMarketVO> voList = tplPage.getRecords().stream()
                .map(tpl -> convertToVO(tpl, userId))
                .collect(Collectors.toList());

        Page<TemplateMarketVO> result = new Page<>(tplPage.getCurrent(), tplPage.getSize(), tplPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public TemplateMarketVO getTemplateDetail(String templateId) {
        VideoTemplate tpl = videoTemplateMapper.selectById(templateId);
        if (tpl == null) {
            throw new BusinessException(10020, "模版不存在");
        }
        String userId = null;
        try { userId = StpUtil.getLoginIdAsString(); } catch (Exception ignored) {}
        return convertToVO(tpl, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> purchaseTemplate(String templateId) {
        String userId = StpUtil.getLoginIdAsString();

        VideoTemplate tpl = videoTemplateMapper.selectById(templateId);
        if (tpl == null || tpl.getStatus() != 1) {
            throw new BusinessException(10020, "模版不存在或已下架");
        }

        // 检查是否已购买
        Long existCount = purchasedMapper.selectCount(new LambdaQueryWrapper<UserPurchasedTemplate>()
                .eq(UserPurchasedTemplate::getUserId, userId)
                .eq(UserPurchasedTemplate::getTemplateId, templateId));
        if (existCount > 0) {
            throw new BusinessException(10021, "您已购买过此模版");
        }

        BigDecimal price = tpl.getPrice() != null ? tpl.getPrice() : BigDecimal.ZERO;

        // 收费模版需扣余额
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            boolean success = walletService.deductBalance(userId, price, templateId, "购买模版: " + tpl.getTemplateName());
            if (!success) {
                throw new BusinessException(40001, "余额不足，请先充值");
            }
        }

        // 记录购买关系
        UserPurchasedTemplate record = new UserPurchasedTemplate();
        record.setUserId(userId);
        record.setTemplateId(templateId);
        record.setPricePaid(price);
        purchasedMapper.insert(record);

        // 使用次数+1
        tpl.setUsageCount((tpl.getUsageCount() != null ? tpl.getUsageCount() : 0) + 1);
        videoTemplateMapper.updateById(tpl);

        Map<String, Object> result = new HashMap<>();
        result.put("templateId", templateId);
        result.put("pricePaid", price);
        result.put("message", price.compareTo(BigDecimal.ZERO) > 0 ? "购买成功" : "免费获取成功");
        return result;
    }

    /**
     * 实体转 VO, 附加购买/点赞状态
     */
    private TemplateMarketVO convertToVO(VideoTemplate tpl, String userId) {
        TemplateMarketVO vo = new TemplateMarketVO();
        BeanUtils.copyProperties(tpl, vo);
        vo.setTemplateId(tpl.getId());

        // 查询创建者名称
        if (StringUtils.hasText(tpl.getCreatorId())) {
            User creator = userMapper.selectById(tpl.getCreatorId());
            vo.setCreatorName(creator != null ? creator.getUsername() : "未知");
        }

        if (StringUtils.hasText(userId)) {
            // 是否已购买
            Long purchased = purchasedMapper.selectCount(new LambdaQueryWrapper<UserPurchasedTemplate>()
                    .eq(UserPurchasedTemplate::getUserId, userId)
                    .eq(UserPurchasedTemplate::getTemplateId, tpl.getId()));
            vo.setPurchased(purchased > 0);

            // 是否已点赞
            Long liked = interactionMapper.selectCount(new LambdaQueryWrapper<UserInteraction>()
                    .eq(UserInteraction::getUserId, userId)
                    .eq(UserInteraction::getTargetId, tpl.getId())
                    .eq(UserInteraction::getActionType, "LIKE_TEMPLATE"));
            vo.setLiked(liked > 0);
        } else {
            vo.setPurchased(false);
            vo.setLiked(false);
        }
        return vo;
    }
}

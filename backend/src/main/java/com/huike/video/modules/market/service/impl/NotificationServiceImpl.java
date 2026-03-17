package com.huike.video.modules.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.market.entity.Notification;
import com.huike.video.modules.market.mapper.NotificationMapper;
import com.huike.video.modules.market.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public Page<Notification> getMyNotifications(int page, int size, String type) {
        String userId = StpUtil.getLoginIdAsString();
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId);
        wrapper.eq(StringUtils.hasText(type), Notification::getType, type);
        wrapper.orderByDesc(Notification::getCreatedAt);
        return notificationMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Boolean markAsRead(Long notificationId) {
        String userId = StpUtil.getLoginIdAsString();
        int updated = notificationMapper.update(null, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getId, notificationId)
                .eq(Notification::getUserId, userId)
                .set(Notification::getIsRead, 1));
        return updated > 0;
    }

    @Override
    public Boolean markAllAsRead() {
        String userId = StpUtil.getLoginIdAsString();
        notificationMapper.update(null, new LambdaUpdateWrapper<Notification>()
                .eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0)
                .set(Notification::getIsRead, 1));
        return true;
    }

    @Override
    public void sendNotification(String userId, String type, String title, String content, String relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
        log.info("发送通知: userId={}, type={}, title={}", userId, type, title);
    }
}

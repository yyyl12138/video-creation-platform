package com.huike.video.modules.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huike.video.modules.market.entity.Notification;

public interface NotificationService {

    /**
     * 分页获取当前用户的消息列表
     */
    Page<Notification> getMyNotifications(int page, int size, String type);

    /**
     * 标记单条通知为已读
     */
    Boolean markAsRead(Long notificationId);

    /**
     * 全部标记为已读
     */
    Boolean markAllAsRead();

    /**
     * 发送通知 (供其他 Service 内部调用)
     */
    void sendNotification(String userId, String type, String title, String content, String relatedId);
}

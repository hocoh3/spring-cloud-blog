package com.blog.interaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.interaction.entity.Notification;

import java.util.List;

public interface NotificationService extends IService<Notification> {

    long getUnreadCount(Long userId);

    List<Notification> getNotifications(Long userId, int page, int size);

    boolean markAsRead(Long id);

    boolean markAllAsRead(Long userId);

    boolean sendNotification(Notification notification);

    void sendWebSocketNotification(Long userId, Notification notification);

    List<Notification> getAllNotifications(int page, int size);

    boolean broadcastNotification(Notification notification);
}

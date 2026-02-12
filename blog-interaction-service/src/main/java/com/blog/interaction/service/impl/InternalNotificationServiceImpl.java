package com.blog.interaction.service.impl;

import com.blog.interaction.entity.Notification;
import com.blog.interaction.service.InternalNotificationService;
import com.blog.interaction.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class InternalNotificationServiceImpl implements InternalNotificationService {

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean sendNotification(Map<String, Object> notification) {
        Notification notif = new Notification();
        notif.setUserId(Long.valueOf(notification.get("userId").toString()));
        notif.setTitle((String) notification.get("title"));
        notif.setContent((String) notification.get("content"));
        notif.setType(notification.get("type") != null ? Integer.valueOf(notification.get("type").toString()) : 0);
        notif.setRelatedId(notification.get("relatedId") != null ? Long.valueOf(notification.get("relatedId").toString()) : null);
        return notificationService.sendNotification(notif);
    }
}

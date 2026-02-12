package com.blog.interaction.service;

import java.util.Map;

public interface InternalNotificationService {
    boolean sendNotification(Map<String, Object> notification);
}

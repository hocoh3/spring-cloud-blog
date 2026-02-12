package com.blog.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.interaction.entity.Notification;
import com.blog.interaction.feign.UserClient;
import com.blog.interaction.mapper.NotificationMapper;
import com.blog.interaction.service.NotificationService;
import com.blog.interaction.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    private final WebSocketServer webSocketServer;
    private final UserClient userClient;

    @Override
    public long getUnreadCount(Long userId) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .eq("is_read", 0);
        return count(queryWrapper);
    }

    @Override
    public List<Notification> getNotifications(Long userId, int page, int size) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT " + (page * size) + ", " + size);
        return list(queryWrapper);
    }

    @Override
    public boolean markAsRead(Long id) {
        Notification notification = getById(id);
        if (notification != null) {
            notification.setIsRead(1);
            notification.setUpdateTime(LocalDateTime.now());
            return updateById(notification);
        }
        return false;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        Notification notification = new Notification();
        notification.setIsRead(1);
        notification.setUpdateTime(LocalDateTime.now());
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return update(notification, queryWrapper);
    }

    @Override
    public boolean sendNotification(Notification notification) {
        notification.setIsRead(0);
        notification.setCreateTime(LocalDateTime.now());
        notification.setUpdateTime(LocalDateTime.now());
        notification.setStatus(1);
        if (notification.getType() == null) {
            notification.setType(0);
        }
        boolean saved = save(notification);
        if (saved) {
            sendWebSocketNotification(notification.getUserId(), notification);
        }
        return saved;
    }

    @Override
    public void sendWebSocketNotification(Long userId, Notification notification) {
        webSocketServer.sendNotificationToUser(userId.toString(), notification);
    }

    @Override
    public List<Notification> getAllNotifications(int page, int size) {
        QueryWrapper<Notification> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time")
                .last("LIMIT " + (page * size) + ", " + size);
        return list(queryWrapper);
    }

    @Override
    public boolean broadcastNotification(Notification notification) {
        try {
            log.info("开始广播通知，标题: {}", notification.getTitle());
            ResponseEntity<java.util.List<UserClient.UserDTO>> response = userClient.getAllUsers();
            
            if (response == null) {
                log.error("获取用户列表失败：响应为null");
                return false;
            }
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("获取用户列表失败，状态码: {}", response.getStatusCode());
                return false;
            }
            
            if (response.getBody() == null || response.getBody().isEmpty()) {
                log.error("获取用户列表失败：响应体为空");
                return false;
            }
            
            java.util.List<UserClient.UserDTO> users = response.getBody();
            log.info("成功获取用户列表，用户数量: {}", users.size());

            int successCount = 0;
            for (UserClient.UserDTO user : users) {
                try {
                    Notification userNotification = new Notification();
                    userNotification.setUserId(user.getId());
                    userNotification.setTitle(notification.getTitle());
                    userNotification.setContent(notification.getContent());
                    userNotification.setRelatedId(notification.getRelatedId());
                    userNotification.setType(notification.getType() != null ? notification.getType() : 0);
                    userNotification.setIsRead(0);
                    userNotification.setStatus(1);
                    userNotification.setCreateTime(LocalDateTime.now());
                    userNotification.setUpdateTime(LocalDateTime.now());
                    
                    boolean saved = save(userNotification);
                    if (saved) {
                        successCount++;
                        webSocketServer.sendNotificationToUser(user.getId().toString(), userNotification);
                    }
                } catch (Exception e) {
                    log.error("为用户 {} 创建通知失败", user.getId(), e);
                }
            }
            
            log.info("广播通知完成，成功: {}，总数: {}", successCount, users.size());
            return successCount > 0;
        } catch (Exception e) {
            log.error("广播通知失败: {}", e.getMessage(), e);
            return false;
        }
    }
}

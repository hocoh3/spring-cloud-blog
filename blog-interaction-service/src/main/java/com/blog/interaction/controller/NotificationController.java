package com.blog.interaction.controller;

import com.blog.interaction.entity.Notification;
import com.blog.interaction.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/unread/count/{userId}")
    public long getUnreadCount(@PathVariable Long userId) {
        return notificationService.getUnreadCount(userId);
    }

    @GetMapping("/user/{userId}")
    public List<Notification> getNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return notificationService.getNotifications(userId, page, size);
    }

    @PutMapping("/read/{id}")
    public boolean markAsRead(@PathVariable Long id) {
        return notificationService.markAsRead(id);
    }

    @PutMapping("/read/all/{userId}")
    public boolean markAllAsRead(@PathVariable Long userId) {
        return notificationService.markAllAsRead(userId);
    }

    @PostMapping("/send")
    public boolean sendNotification(@RequestBody Notification notification) {
        return notificationService.sendNotification(notification);
    }

    @DeleteMapping("/{id}")
    public boolean deleteNotification(@PathVariable Long id) {
        return notificationService.removeById(id);
    }

    @GetMapping("/notifications")
    public List<Notification> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return notificationService.getAllNotifications(page, size);
    }

    @PostMapping("/broadcast")
    public boolean broadcastNotification(@RequestBody Notification notification) {
        return notificationService.broadcastNotification(notification);
    }

    @PostMapping("/notifications")
    public boolean createNotification(@RequestBody Notification notification) {
        return notificationService.sendNotification(notification);
    }
}

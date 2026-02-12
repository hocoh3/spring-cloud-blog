package com.blog.interaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.interaction.entity.Message;

import java.util.List;

public interface MessageService extends IService<Message> {

    long getUnreadCount(Long userId);

    List<Message> getMessages(Long userId, int page, int size);

    boolean markAsRead(Long id);

    boolean markAllAsRead(Long userId);

    boolean sendMessage(Message message);

    void sendWebSocketMessage(Long receiverId, Message message);

    List<Message> getAllMessages(int page, int size);
}

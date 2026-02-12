package com.blog.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.interaction.entity.Message;
import com.blog.interaction.feign.UserClient;
import com.blog.interaction.mapper.MessageMapper;
import com.blog.interaction.service.MessageService;
import com.blog.interaction.websocket.WebSocketServer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final WebSocketServer webSocketServer;
    private final UserClient userClient;

    @Override
    public long getUnreadCount(Long userId) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId)
                .eq("is_read", 0);
        return count(queryWrapper);
    }

    @Override
    public List<Message> getMessages(Long userId, int page, int size) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT " + (page * size) + ", " + size);
        return list(queryWrapper);
    }

    @Override
    public boolean markAsRead(Long id) {
        Message message = getById(id);
        if (message != null) {
            message.setIsRead(1);
            message.setUpdateTime(LocalDateTime.now());
            return updateById(message);
        }
        return false;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        Message message = new Message();
        message.setIsRead(1);
        message.setUpdateTime(LocalDateTime.now());
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("receiver_id", userId);
        return update(message, queryWrapper);
    }

    @Override
    public boolean sendMessage(Message message) {
        try {
            ResponseEntity<UserClient.UserDTO> response = userClient.getUserById(message.getSenderId());
            if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                UserClient.UserDTO sender = response.getBody();
                message.setSenderName(sender.getNickname() != null ? sender.getNickname() : sender.getUsername());
                message.setSenderAvatar(sender.getAvatar());
            }
        } catch (Exception e) {
            System.err.println("获取发送者信息失败: " + e.getMessage());
        }
        
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setStatus(1);
        boolean saved = save(message);
        if (saved) {
            webSocketServer.sendMessageToUserWithType(message.getReceiverId().toString(), message);
        }
        return saved;
    }

    @Override
    public void sendWebSocketMessage(Long receiverId, Message message) {
        webSocketServer.sendMessageToUserWithType(receiverId.toString(), message);
    }

    @Override
    public List<Message> getAllMessages(int page, int size) {
        QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time")
                .last("LIMIT " + (page * size) + ", " + size);
        return list(queryWrapper);
    }
}

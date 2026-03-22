package com.blog.interaction.service.impl;

import com.blog.interaction.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RabbitMQListener {

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotification(String message) {
        System.out.println("[RabbitMQ] 收到通知消息: " + message);
        System.out.println("[RabbitMQ] 处理时间: " + LocalDateTime.now());
        // 这里可以添加具体的通知处理逻辑
    }

    @RabbitListener(queues = RabbitMQConfig.COMMENT_QUEUE)
    public void handleComment(String message) {
        System.out.println("[RabbitMQ] 收到评论消息: " + message);
        System.out.println("[RabbitMQ] 处理时间: " + LocalDateTime.now());
        // 这里可以添加具体的评论处理逻辑
    }

    @RabbitListener(queues = RabbitMQConfig.MESSAGE_QUEUE)
    public void handleMessage(String message) {
        System.out.println("[RabbitMQ] 收到私信消息: " + message);
        System.out.println("[RabbitMQ] 处理时间: " + LocalDateTime.now());
        // 这里可以添加具体的私信处理逻辑
    }
}
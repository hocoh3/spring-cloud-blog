package com.blog.interaction.service;

public interface RabbitMQService {
    /**
     * 发送通知消息
     * @param message 消息内容
     */
    void sendNotification(String message);

    /**
     * 发送评论消息
     * @param message 消息内容
     */
    void sendComment(String message);

    /**
     * 发送私信消息
     * @param message 消息内容
     */
    void sendMessage(String message);
}
package com.blog.interaction.service.impl;

import com.blog.interaction.config.RabbitMQConfig;
import com.blog.interaction.service.RabbitMQService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQServiceImpl implements RabbitMQService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendNotification(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE,
                RabbitMQConfig.NOTIFICATION_ROUTING_KEY,
                message
        );
    }

    @Override
    public void sendComment(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COMMENT_EXCHANGE,
                RabbitMQConfig.COMMENT_ROUTING_KEY,
                message
        );
    }

    @Override
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.MESSAGE_EXCHANGE,
                RabbitMQConfig.MESSAGE_ROUTING_KEY,
                message
        );
    }
}
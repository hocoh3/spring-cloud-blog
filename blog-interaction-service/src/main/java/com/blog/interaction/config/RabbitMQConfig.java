package com.blog.interaction.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // 队列名称
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String COMMENT_QUEUE = "comment.queue";
    public static final String MESSAGE_QUEUE = "message.queue";

    // 交换机名称
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String COMMENT_EXCHANGE = "comment.exchange";
    public static final String MESSAGE_EXCHANGE = "message.exchange";

    // 路由键
    public static final String NOTIFICATION_ROUTING_KEY = "notification.key";
    public static final String COMMENT_ROUTING_KEY = "comment.key";
    public static final String MESSAGE_ROUTING_KEY = "message.key";

    // 创建通知队列
    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    // 创建评论队列
    @Bean
    public Queue commentQueue() {
        return new Queue(COMMENT_QUEUE, true);
    }

    // 创建消息队列
    @Bean
    public Queue messageQueue() {
        return new Queue(MESSAGE_QUEUE, true);
    }

    // 创建通知交换机
    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    // 创建评论交换机
    @Bean
    public DirectExchange commentExchange() {
        return new DirectExchange(COMMENT_EXCHANGE);
    }

    // 创建消息交换机
    @Bean
    public DirectExchange messageExchange() {
        return new DirectExchange(MESSAGE_EXCHANGE);
    }

    // 绑定通知队列到交换机
    @Bean
    public Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with(NOTIFICATION_ROUTING_KEY);
    }

    // 绑定评论队列到交换机
    @Bean
    public Binding commentBinding(Queue commentQueue, DirectExchange commentExchange) {
        return BindingBuilder.bind(commentQueue).to(commentExchange).with(COMMENT_ROUTING_KEY);
    }

    // 绑定消息队列到交换机
    @Bean
    public Binding messageBinding(Queue messageQueue, DirectExchange messageExchange) {
        return BindingBuilder.bind(messageQueue).to(messageExchange).with(MESSAGE_ROUTING_KEY);
    }
}
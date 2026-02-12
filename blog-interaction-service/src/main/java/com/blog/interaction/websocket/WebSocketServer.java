package com.blog.interaction.websocket;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketServer extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> USER_SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            USER_SESSIONS.put(userId, session);
            log.info("用户[{}]建立WebSocket连接，当前在线用户数：{}", userId, USER_SESSIONS.size());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String userId = getUserIdFromSession(session);
        String payload = message.getPayload();
        log.info("收到用户[{}]的消息：{}", userId, payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = getUserIdFromSession(session);
        if (userId != null) {
            USER_SESSIONS.remove(userId);
            log.info("用户[{}]断开WebSocket连接，当前在线用户数：{}", userId, USER_SESSIONS.size());
        }
    }

    public void sendMessageToUser(String userId, Object message) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String jsonMessage = JSON.toJSONString(message);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("发送消息给用户[{}]成功：{}", userId, jsonMessage);
            } catch (Exception e) {
                log.error("发送消息给用户[{}]失败：{}", userId, e.getMessage());
            }
        } else {
            log.warn("用户[{}]的WebSocket连接不存在或已关闭", userId);
        }
    }

    public void sendNotificationToUser(String userId, Object notification) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> messageWrapper = new HashMap<>();
                messageWrapper.put("type", "notification");
                messageWrapper.put("data", notification);
                String jsonMessage = JSON.toJSONString(messageWrapper);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("发送通知给用户[{}]成功：{}", userId, jsonMessage);
            } catch (Exception e) {
                log.error("发送通知给用户[{}]失败：{}", userId, e.getMessage());
            }
        } else {
            log.warn("用户[{}]的WebSocket连接不存在或已关闭", userId);
        }
    }

    public void sendMessageToUserWithType(String userId, Object message) {
        WebSocketSession session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                Map<String, Object> messageWrapper = new HashMap<>();
                messageWrapper.put("type", "message");
                messageWrapper.put("data", message);
                String jsonMessage = JSON.toJSONString(messageWrapper);
                session.sendMessage(new TextMessage(jsonMessage));
                log.info("发送私信给用户[{}]成功：{}", userId, jsonMessage);
            } catch (Exception e) {
                log.error("发送私信给用户[{}]失败：{}", userId, e.getMessage());
            }
        } else {
            log.warn("用户[{}]的WebSocket连接不存在或已关闭", userId);
        }
    }

    public void sendMessageToAll(Object message) {
        String jsonMessage = JSON.toJSONString(message);
        USER_SESSIONS.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(jsonMessage));
                } catch (Exception e) {
                    log.error("发送消息给用户失败：{}", e.getMessage());
                }
            }
        });
    }

    private String getUserIdFromSession(WebSocketSession session) {
        return (String) session.getAttributes().get("userId");
    }
}

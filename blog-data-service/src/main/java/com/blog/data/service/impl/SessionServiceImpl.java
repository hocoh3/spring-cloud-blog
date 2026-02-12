package com.blog.data.service.impl;

import com.blog.data.service.SessionService;
import com.blog.data.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private RedisUtil redisUtil;

    private static final String SESSION_PREFIX = "session:";
    private static final long SESSION_TIMEOUT = 86400;

    @Override
    public String createSession(Long userId, Map<String, Object> data) {
        String sessionId = SESSION_PREFIX + UUID.randomUUID().toString();
        Map<String, Object> sessionData = new HashMap<>(data);
        sessionData.put("userId", userId);
        sessionData.put("createTime", System.currentTimeMillis());
        sessionData.put("lastAccessTime", System.currentTimeMillis());
        
        redisUtil.set(sessionId, sessionData, SESSION_TIMEOUT, TimeUnit.SECONDS);
        return sessionId;
    }

    @Override
    public boolean updateSession(String sessionId, Map<String, Object> data) {
        try {
            String fullSessionId = sessionId.startsWith(SESSION_PREFIX) ? sessionId : SESSION_PREFIX + sessionId;
            Map<String, Object> sessionData = (Map<String, Object>) redisUtil.get(fullSessionId);
            if (sessionData != null) {
                sessionData.putAll(data);
                sessionData.put("lastAccessTime", System.currentTimeMillis());
                redisUtil.set(fullSessionId, sessionData, SESSION_TIMEOUT, TimeUnit.SECONDS);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Object> getSession(String sessionId) {
        String fullSessionId = sessionId.startsWith(SESSION_PREFIX) ? sessionId : SESSION_PREFIX + sessionId;
        Map<String, Object> sessionData = (Map<String, Object>) redisUtil.get(fullSessionId);
        if (sessionData != null) {
            sessionData.put("lastAccessTime", System.currentTimeMillis());
            redisUtil.set(fullSessionId, sessionData, SESSION_TIMEOUT, TimeUnit.SECONDS);
        }
        return sessionData;
    }

    @Override
    public boolean deleteSession(String sessionId) {
        String fullSessionId = sessionId.startsWith(SESSION_PREFIX) ? sessionId : SESSION_PREFIX + sessionId;
        return redisUtil.delete(fullSessionId);
    }

    @Override
    public boolean refreshSession(String sessionId) {
        String fullSessionId = sessionId.startsWith(SESSION_PREFIX) ? sessionId : SESSION_PREFIX + sessionId;
        return redisUtil.expire(fullSessionId, SESSION_TIMEOUT, TimeUnit.SECONDS);
    }

    @Override
    public boolean validateSession(String sessionId) {
        String fullSessionId = sessionId.startsWith(SESSION_PREFIX) ? sessionId : SESSION_PREFIX + sessionId;
        return redisUtil.hasKey(fullSessionId);
    }

    @Override
    public Long getUserIdBySession(String sessionId) {
        String fullSessionId = sessionId.startsWith(SESSION_PREFIX) ? sessionId : SESSION_PREFIX + sessionId;
        Map<String, Object> sessionData = (Map<String, Object>) redisUtil.get(fullSessionId);
        if (sessionData != null) {
            return (Long) sessionData.get("userId");
        }
        return null;
    }
}

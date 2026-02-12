package com.blog.data.service;

import java.util.Map;

public interface SessionService {

    String createSession(Long userId, Map<String, Object> data);

    boolean updateSession(String sessionId, Map<String, Object> data);

    Map<String, Object> getSession(String sessionId);

    boolean deleteSession(String sessionId);

    boolean refreshSession(String sessionId);

    boolean validateSession(String sessionId);

    Long getUserIdBySession(String sessionId);
}

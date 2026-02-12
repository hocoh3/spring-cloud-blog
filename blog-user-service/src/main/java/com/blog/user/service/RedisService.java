package com.blog.user.service;

public interface RedisService {
    
    void set(String key, Object value);
    
    void set(String key, Object value, long timeout);
    
    Object get(String key);
    
    boolean delete(String key);
    
    boolean hasKey(String key);
    
    boolean expire(String key, long timeout);
    
    long getExpire(String key);
}

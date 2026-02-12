package com.blog.data.service;

import java.util.List;
import java.util.Map;

public interface CacheService {

    boolean cacheData(String key, Object value);

    boolean cacheData(String key, Object value, long timeout);

    Object getCacheData(String key);

    boolean deleteCache(String key);

    boolean exists(String key);

    boolean expire(String key, long timeout);

    Map<String, Object> batchGet(List<String> keys);

    boolean batchSet(Map<String, Object> data);

    boolean batchDelete(List<String> keys);
}

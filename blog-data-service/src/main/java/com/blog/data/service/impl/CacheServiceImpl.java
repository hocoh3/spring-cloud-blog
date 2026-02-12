package com.blog.data.service.impl;

import com.blog.data.service.CacheService;
import com.blog.data.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private RedisUtil redisUtil;

    private static final long DEFAULT_TIMEOUT = 3600;

    @Override
    public boolean cacheData(String key, Object value) {
        try {
            redisUtil.set(key, value, DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean cacheData(String key, Object value, long timeout) {
        try {
            redisUtil.set(key, value, timeout, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Object getCacheData(String key) {
        return redisUtil.get(key);
    }

    @Override
    public boolean deleteCache(String key) {
        return redisUtil.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return redisUtil.hasKey(key);
    }

    @Override
    public boolean expire(String key, long timeout) {
        return redisUtil.expire(key, timeout, TimeUnit.SECONDS);
    }

    @Override
    public Map<String, Object> batchGet(List<String> keys) {
        Map<String, Object> result = new HashMap<>();
        for (String key : keys) {
            result.put(key, redisUtil.get(key));
        }
        return result;
    }

    @Override
    public boolean batchSet(Map<String, Object> data) {
        try {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                redisUtil.set(entry.getKey(), entry.getValue(), DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean batchDelete(List<String> keys) {
        try {
            redisUtil.del(keys.toArray(new String[0]));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

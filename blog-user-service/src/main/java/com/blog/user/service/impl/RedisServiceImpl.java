package com.blog.user.service.impl;

import com.blog.user.feign.DataClient;
import com.blog.user.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private DataClient dataClient;

    @Override
    public void set(String key, Object value) {
        dataClient.cacheData(key, value);
    }

    @Override
    public void set(String key, Object value, long timeout) {
        dataClient.cacheDataWithTimeout(key, value, timeout);
    }

    @Override
    public Object get(String key) {
        return dataClient.getCacheData(key);
    }

    @Override
    public boolean delete(String key) {
        return dataClient.deleteCache(key);
    }

    @Override
    public boolean hasKey(String key) {
        return dataClient.exists(key);
    }

    @Override
    public boolean expire(String key, long timeout) {
        return dataClient.expire(key, timeout);
    }

    @Override
    public long getExpire(String key) {
        return 0;
    }
}

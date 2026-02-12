package com.blog.data.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
    }

    public long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public void del(String... keys) {
        if (keys != null && keys.length > 0) {
            redisTemplate.delete(Arrays.asList(keys));
        }
    }

    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public boolean hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public boolean hSetAll(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void hDel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    public boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    public long lPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    public boolean lRemove(String key, long count, Object value) {
        try {
            redisTemplate.opsForList().remove(key, count, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean zAdd(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean zAdd(String key, Object value, long score) {
        try {
            redisTemplate.opsForZSet().add(key, value, (double) score);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public long zRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    public long zRank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    public long zReverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    public Set<Object> zRange(String key, long start, long end) {
        Set<Object> result = redisTemplate.opsForZSet().range(key, start, end);
        return result != null ? result : new java.util.HashSet<>();
    }

    public Set<Object> zReverseRange(String key, long start, long end) {
        Set<Object> result = redisTemplate.opsForZSet().reverseRange(key, start, end);
        return result != null ? result : new java.util.HashSet<>();
    }

    public long zSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    public long increment(String key) {
        if (!hasKey(key)) {
            set(key, "0");
        }
        Long result = redisTemplate.opsForValue().increment(key);
        return result != null ? result : 0;
    }

    public long increment(String key, long delta) {
        if (!hasKey(key)) {
            set(key, "0");
        }
        Long result = redisTemplate.opsForValue().increment(key, delta);
        return result != null ? result : 0;
    }

    public long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    public long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
}

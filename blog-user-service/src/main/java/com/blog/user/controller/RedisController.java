package com.blog.user.controller;

import com.blog.user.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;

    @PostMapping("/set")
    public Map<String, Object> set(@RequestParam String key, @RequestParam String value) {
        redisService.set(key, value);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "设置成功");
        return result;
    }

    @PostMapping("/setWithExpire")
    public Map<String, Object> setWithExpire(@RequestParam String key, @RequestParam String value, @RequestParam long timeout) {
        redisService.set(key, value, timeout);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "设置成功，过期时间：" + timeout + "秒");
        return result;
    }

    @GetMapping("/get")
    public Map<String, Object> get(@RequestParam String key) {
        Object value = redisService.get(key);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("value", value);
        return result;
    }

    @DeleteMapping("/delete")
    public Map<String, Object> delete(@RequestParam String key) {
        boolean deleted = redisService.delete(key);
        Map<String, Object> result = new HashMap<>();
        result.put("success", deleted);
        result.put("message", deleted ? "删除成功" : "删除失败");
        return result;
    }

    @GetMapping("/hasKey")
    public Map<String, Object> hasKey(@RequestParam String key) {
        boolean exists = redisService.hasKey(key);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("exists", exists);
        return result;
    }

    @PostMapping("/expire")
    public Map<String, Object> expire(@RequestParam String key, @RequestParam long timeout) {
        boolean expired = redisService.expire(key, timeout);
        Map<String, Object> result = new HashMap<>();
        result.put("success", expired);
        result.put("message", expired ? "设置过期时间成功" : "设置过期时间失败");
        return result;
    }
}

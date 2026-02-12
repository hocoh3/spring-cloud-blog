package com.blog.data.controller;

import com.blog.data.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @PostMapping
    public ResponseEntity<Boolean> cacheData(@RequestBody Map<String, Object> request) {
        String key = (String) request.get("key");
        Object value = request.get("value");
        Long timeout = request.get("timeout") != null ? ((Number) request.get("timeout")).longValue() : null;
        
        boolean result;
        if (timeout != null) {
            result = cacheService.cacheData(key, value, timeout);
        } else {
            result = cacheService.cacheData(key, value);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{key}")
    public ResponseEntity<Object> getCacheData(@PathVariable String key) {
        Object data = cacheService.getCacheData(key);
        return ResponseEntity.ok(data);
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Boolean> deleteCache(@PathVariable String key) {
        boolean result = cacheService.deleteCache(key);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/exists/{key}")
    public ResponseEntity<Boolean> exists(@PathVariable String key) {
        boolean result = cacheService.exists(key);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/expire")
    public ResponseEntity<Boolean> expire(@RequestParam String key, @RequestParam long timeout) {
        boolean result = cacheService.expire(key, timeout);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> batchGet(@RequestBody List<String> keys) {
        Map<String, Object> result = cacheService.batchGet(keys);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/batch/set")
    public ResponseEntity<Boolean> batchSet(@RequestBody Map<String, Object> data) {
        boolean result = cacheService.batchSet(data);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Boolean> batchDelete(@RequestBody List<String> keys) {
        boolean result = cacheService.batchDelete(keys);
        return ResponseEntity.ok(result);
    }
}

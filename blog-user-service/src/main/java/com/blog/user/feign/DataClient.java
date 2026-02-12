package com.blog.user.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "data-service")
public interface DataClient {

    @PostMapping("/cache")
    Boolean cacheData(@RequestParam("key") String key, @RequestBody Object value);

    @PostMapping("/cache/timeout")
    Boolean cacheDataWithTimeout(@RequestParam("key") String key, @RequestBody Object value, @RequestParam("timeout") long timeout);

    @GetMapping("/cache/{key}")
    Object getCacheData(@PathVariable("key") String key);

    @DeleteMapping("/cache/{key}")
    Boolean deleteCache(@PathVariable("key") String key);

    @GetMapping("/cache/exists/{key}")
    Boolean exists(@PathVariable("key") String key);

    @PostMapping("/cache/expire")
    Boolean expire(@RequestParam("key") String key, @RequestParam("timeout") long timeout);

    @PostMapping("/cache/batch")
    Map<String, Object> batchGet(@RequestBody List<String> keys);

    @PostMapping("/cache/batch/set")
    Boolean batchSet(@RequestBody Map<String, Object> data);

    @DeleteMapping("/cache/batch")
    Boolean batchDelete(@RequestBody List<String> keys);

    @PostMapping("/session")
    String createSession(@RequestParam("userId") Long userId, @RequestBody Map<String, Object> data);

    @PutMapping("/session/{sessionId}")
    Boolean updateSession(@PathVariable("sessionId") String sessionId, @RequestBody Map<String, Object> data);

    @GetMapping("/session/{sessionId}")
    Map<String, Object> getSession(@PathVariable("sessionId") String sessionId);

    @DeleteMapping("/session/{sessionId}")
    Boolean deleteSession(@PathVariable("sessionId") String sessionId);

    @PostMapping("/session/refresh/{sessionId}")
    Boolean refreshSession(@PathVariable("sessionId") String sessionId);

    @GetMapping("/session/validate/{sessionId}")
    Boolean validateSession(@PathVariable("sessionId") String sessionId);

    @GetMapping("/session/user/{sessionId}")
    Long getUserIdBySession(@PathVariable("sessionId") String sessionId);
}

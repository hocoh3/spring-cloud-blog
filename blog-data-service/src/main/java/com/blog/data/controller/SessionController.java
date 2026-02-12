package com.blog.data.controller;

import com.blog.data.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @PostMapping
    public ResponseEntity<String> createSession(@RequestParam Long userId, @RequestBody Map<String, Object> data) {
        String sessionId = sessionService.createSession(userId, data);
        return ResponseEntity.ok(sessionId);
    }

    @PutMapping("/{sessionId}")
    public ResponseEntity<Boolean> updateSession(
            @PathVariable String sessionId,
            @RequestBody Map<String, Object> data) {
        boolean result = sessionService.updateSession(sessionId, data);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSession(@PathVariable String sessionId) {
        Map<String, Object> sessionData = sessionService.getSession(sessionId);
        return ResponseEntity.ok(sessionData);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Boolean> deleteSession(@PathVariable String sessionId) {
        boolean result = sessionService.deleteSession(sessionId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/refresh/{sessionId}")
    public ResponseEntity<Boolean> refreshSession(@PathVariable String sessionId) {
        boolean result = sessionService.refreshSession(sessionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/validate/{sessionId}")
    public ResponseEntity<Boolean> validateSession(@PathVariable String sessionId) {
        boolean result = sessionService.validateSession(sessionId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/{sessionId}")
    public ResponseEntity<Long> getUserIdBySession(@PathVariable String sessionId) {
        Long userId = sessionService.getUserIdBySession(sessionId);
        return ResponseEntity.ok(userId);
    }
}

package com.blog.admin.controller;

import com.blog.admin.feign.CommentClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/comments")
public class CommentManagementController {

    @Autowired
    private CommentClient commentClient;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            String response = commentClient.getAllComments(page, size);
            Map<String, Object> body = objectMapper.readValue(response, Map.class);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "获取评论列表失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCommentCount() {
        try {
            String response = commentClient.getCommentCount();
            Map<String, Object> body = objectMapper.readValue(response, Map.class);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "获取评论数量失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateCommentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        try {
            String response = commentClient.updateCommentStatus(id, request.get("status"));
            Map<String, Object> body = objectMapper.readValue(response, Map.class);
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "更新评论状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long id) {
        try {
            String response = commentClient.deleteComment(id);
            Map<String, Object> result = new HashMap<>();
            result.put("success", Boolean.parseBoolean(response));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "删除评论失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
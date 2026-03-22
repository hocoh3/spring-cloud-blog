package com.blog.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.admin.feign.UserClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        // 注册Java 8时间模块，确保时间类型正确序列化
        objectMapper.registerModule(new JavaTimeModule());
    }

    @GetMapping
    public ResponseEntity<Page> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            String response = userClient.getUserList(page, size, keyword);
            // 确保使用UTF-8编码解析响应
            Page pageData = objectMapper.readValue(response.getBytes(StandardCharsets.UTF_8), Page.class);
            return ResponseEntity.ok(pageData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        try {
            String response = userClient.getUserById(id);
            // 确保使用UTF-8编码解析响应
            Object userData = objectMapper.readValue(response.getBytes(StandardCharsets.UTF_8), Object.class);
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Boolean> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            String response = userClient.updateUserStatus(id, status);
            // 确保使用UTF-8编码解析响应
            Boolean result = objectMapper.readValue(response.getBytes(StandardCharsets.UTF_8), Boolean.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
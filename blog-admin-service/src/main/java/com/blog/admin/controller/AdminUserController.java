package com.blog.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.admin.feign.UserClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<Page> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            String response = userClient.getUserList(page, size);
            Page pageData = objectMapper.readValue(response, Page.class);
            return ResponseEntity.ok(pageData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        try {
            String response = userClient.getUserById(id);
            Object userData = objectMapper.readValue(response, Object.class);
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Boolean> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            String response = userClient.updateUserStatus(id, status);
            Boolean result = objectMapper.readValue(response, Boolean.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
package com.blog.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.admin.feign.ContentClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private ContentClient contentClient;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        // 注册Java 8时间模块，确保时间类型正确序列化
        objectMapper.registerModule(new JavaTimeModule());
    }

    @GetMapping
    public ResponseEntity<Page> getCategoryList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        try {
            String response = contentClient.getCategoryList(page, size, keyword);
            // 确保使用UTF-8编码解析响应
            Page pageData = objectMapper.readValue(response.getBytes(StandardCharsets.UTF_8), Page.class);
            return ResponseEntity.ok(pageData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<Boolean> createCategory(@RequestBody Map<String, Object> category) {
        try {
            String response = contentClient.createCategory(category);
            // 确保使用UTF-8编码解析响应
            Boolean result = objectMapper.readValue(response.getBytes(StandardCharsets.UTF_8), Boolean.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateCategory(@PathVariable Long id, @RequestBody Map<String, Object> category) {
        try {
            String response = contentClient.updateCategory(id, category);
            // 确保使用UTF-8编码解析响应
            Boolean result = objectMapper.readValue(response.getBytes(StandardCharsets.UTF_8), Boolean.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCategory(@PathVariable Long id) {
        try {
            String response = contentClient.deleteCategory(id);
            // 确保使用UTF-8编码解析响应
            Boolean result = objectMapper.readValue(response.getBytes(StandardCharsets.UTF_8), Boolean.class);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
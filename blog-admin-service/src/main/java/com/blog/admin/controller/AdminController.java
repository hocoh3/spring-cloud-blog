package com.blog.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.admin.entity.Admin;
import com.blog.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 管理员登录
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            Map<String, Object> response = adminService.login(username, password);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new java.util.HashMap<String, Object>() {{ put("message", e.getMessage()); }});
        }
    }

    // 获取管理员列表
    @GetMapping("/list")
    public ResponseEntity<Page<Admin>> getAdminList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Admin> adminPage = adminService.getAdminList(page, size);
        return ResponseEntity.ok(adminPage);
    }

    // 根据ID获取管理员信息
    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        Admin admin = adminService.getById(id);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        // 隐藏密码
        admin.setPassword(null);
        return ResponseEntity.ok(admin);
    }

    // 创建管理员
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        // 加密密码
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminService.save(admin);
        // 隐藏密码
        admin.setPassword(null);
        return ResponseEntity.ok(admin);
    }

    // 更新管理员信息
    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        admin.setId(id);
        adminService.updateById(admin);
        // 隐藏密码
        admin.setPassword(null);
        return ResponseEntity.ok(admin);
    }

    // 更新管理员状态
    @PutMapping("/{id}/status")
    public ResponseEntity<Boolean> updateAdminStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean result = adminService.updateAdminStatus(id, status);
        return ResponseEntity.ok(result);
    }

    // 重置管理员密码
    @PutMapping("/{id}/reset-password")
    public ResponseEntity<Boolean> resetAdminPassword(@PathVariable Long id, @RequestParam String newPassword) {
        boolean result = adminService.resetAdminPassword(id, newPassword);
        return ResponseEntity.ok(result);
    }

    // 删除管理员
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteAdmin(@PathVariable Long id) {
        boolean result = adminService.removeById(id);
        return ResponseEntity.ok(result);
    }
}

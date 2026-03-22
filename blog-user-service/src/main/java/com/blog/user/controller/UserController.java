package com.blog.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.user.entity.User;
import com.blog.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 获取用户列表（分页）
    @GetMapping
    public ResponseEntity<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<User> userPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(User::getUsername, keyword)
                    .or().like(User::getNickname, keyword)
                    .or().like(User::getEmail, keyword);
            userPage = userService.page(new Page<>(page, size), queryWrapper);
        } else {
            userPage = userService.page(new Page<>(page, size));
        }
        userPage.getRecords().forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(userPage);
    }

    // 获取所有用户（用于@用户功能）
    @GetMapping("/all")
    public ResponseEntity<java.util.List<User>> getAllUsers() {
        log.info("收到获取所有用户的请求");
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getStatus, 1);
        java.util.List<User> users = userService.list(queryWrapper);
        log.info("查询到用户数量: {}", users.size());
        users.forEach(user -> user.setPassword(null));
        log.info("准备返回用户列表，第一个用户ID: {}", users.isEmpty() ? "无" : users.get(0).getId());
        return ResponseEntity.ok(users);
    }

    // 根据ID获取用户信息
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // 隐藏密码
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // 根据用户名获取用户信息
    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // 隐藏密码
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // 用户注册
    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody User user) {
        boolean result = userService.register(user);
        if (!result) {
            return ResponseEntity.badRequest().body(false);
        }
        return ResponseEntity.ok(true);
    }

    // 用户登录
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // 验证用户名和密码
        User user = userService.login(username, password);
        if (user == null) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{ put("message", "用户名或密码错误"); }});
        }

        // 生成JWT令牌
        String token = userService.generateToken(user);

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("tokenType", "Bearer");
        
        // 隐藏密码
        user.setPassword(null);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    // 更新用户信息
    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateUserInfo(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean result = userService.updateUserInfo(user);
        return ResponseEntity.ok(result);
    }

    // 上传用户头像
    @PostMapping("/{id}/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{ put("message", "文件不能为空"); }});
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            String uploadDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File destFile = new File(uploadDir + File.separator + filename);
            file.transferTo(destFile);

            String avatarUrl = "/uploads/avatars/" + filename;

            User user = new User();
            user.setId(id);
            user.setAvatar(avatarUrl);
            userService.updateById(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("avatarUrl", avatarUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(new HashMap<String, Object>() {{ put("message", "文件上传失败: " + e.getMessage()); }});
        }
    }

    // 删除用户
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable Long id) {
        boolean result = userService.removeById(id);
        return ResponseEntity.ok(result);
    }

    // 更新用户状态
    @PutMapping("/{id}/status")
    public ResponseEntity<Boolean> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        user.setUpdateTime(java.time.LocalDateTime.now());
        boolean result = userService.updateById(user);
        return ResponseEntity.ok(result);
    }

    // 修改密码
    @PutMapping("/{id}/password")
    public ResponseEntity<Boolean> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordRequest) {
        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");

        if (currentPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(false);
        }

        // 获取用户信息
        User user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(false);
        }

        // 加密新密码并更新
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(java.time.LocalDateTime.now());
        boolean result = userService.updateById(user);
        return ResponseEntity.ok(result);
    }
}

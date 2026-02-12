package com.blog.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.user.config.JwtConfig;
import com.blog.user.entity.User;
import com.blog.user.mapper.UserMapper;
import com.blog.user.service.UserService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public User getUserByUsername(String username) {
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    @Override
    public User getUserByEmail(String email) {
        return lambdaQuery().eq(User::getEmail, email).one();
    }

    @Override
    public boolean register(User user) {
        // 检查用户名和邮箱是否已存在
        if (getUserByUsername(user.getUsername()) != null) {
            return false;
        }
        if (getUserByEmail(user.getEmail()) != null) {
            return false;
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置默认值
        user.setRole("ROLE_USER");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        // 如果用户没有提供昵称，使用用户名为默认昵称
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            user.setNickname(user.getUsername());
        }

        return save(user);
    }

    @Override
    public boolean updateUserInfo(User user) {
        user.setUpdateTime(LocalDateTime.now());
        // 不允许更新密码和角色
        return lambdaUpdate()
                .set(user.getNickname() != null, User::getNickname, user.getNickname())
                .set(user.getAvatar() != null, User::getAvatar, user.getAvatar())
                .set(user.getBio() != null, User::getBio, user.getBio())
                .eq(User::getId, user.getId())
                .update();
    }

    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = getUserByUsername(username);
        if (user == null) {
            return null;
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }

        // 检查用户状态
        if (user.getStatus() != 1) {
            return null;
        }

        return user;
    }

    @Override
    public String generateToken(User user) {
        // 创建JWT声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", user.getUsername());
        claims.put("userId", user.getId());
        claims.put("role", user.getRole());

        // 生成令牌
        return jwtConfig.generateToken(claims);
    }

    @Override
    public Map<String, Object> getUserInfoFromToken(String token) {
        Map<String, Object> userInfo = new HashMap<>();
        try {
            Claims claims = jwtConfig.getClaimsFromToken(token);
            userInfo.put("username", claims.get("sub"));
            userInfo.put("userId", claims.get("userId"));
            userInfo.put("role", claims.get("role"));
        } catch (Exception e) {
            // 令牌解析失败，返回空映射
        }
        return userInfo;
    }
}

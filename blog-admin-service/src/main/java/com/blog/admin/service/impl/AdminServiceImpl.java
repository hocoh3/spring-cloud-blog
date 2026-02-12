package com.blog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.admin.entity.Admin;
import com.blog.admin.mapper.AdminMapper;
import com.blog.admin.service.AdminService;
import com.blog.admin.service.DataStatisticsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DataStatisticsService dataStatisticsService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpiration;

    @Override
    public Map<String, Object> login(String username, String password) {
        // 根据用户名获取管理员信息
        Admin admin = getAdminByUsername(username);
        if (admin == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 验证管理员状态
        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成JWT令牌
        String token = generateToken(admin);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("tokenType", "Bearer");
        
        // 隐藏密码
        admin.setPassword(null);
        result.put("admin", admin);

        // 收集每日统计数据（每天第一次登录时统计）
        try {
            dataStatisticsService.collectDailyStatistics();
        } catch (Exception e) {
            System.err.println("收集统计数据失败: " + e.getMessage());
        }

        return result;
    }

    @Override
    public String generateToken(Admin admin) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration * 1000);

        // 设置JWT声明
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", admin.getId());
        claims.put("username", admin.getUsername());
        claims.put("role", "admin");

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public Admin getAdminByUsername(String username) {
        return adminMapper.selectByUsername(username);
    }

    @Override
    public Page<Admin> getAdminList(int page, int size) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0)
                .orderByDesc("create_time");
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public boolean updateAdminStatus(Long id, Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        admin.setUpdateTime(LocalDateTime.now());
        return updateById(admin);
    }

    @Override
    public boolean resetAdminPassword(Long id, String newPassword) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setUpdateTime(LocalDateTime.now());
        return updateById(admin);
    }
}

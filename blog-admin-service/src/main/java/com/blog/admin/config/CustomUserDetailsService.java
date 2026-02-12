package com.blog.admin.config;

import com.blog.admin.entity.Admin;
import com.blog.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 根据用户名获取管理员信息
        Admin admin = adminService.getAdminByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException("管理员不存在: " + username);
        }

        // 验证管理员状态
        if (admin.getStatus() == 0) {
            throw new UsernameNotFoundException("管理员账号已被禁用: " + username);
        }

        // 构建UserDetails对象
        return new User(
                admin.getUsername(),
                admin.getPassword(),
                new ArrayList<>() // 权限列表，可以根据实际需求设置
        );
    }
}

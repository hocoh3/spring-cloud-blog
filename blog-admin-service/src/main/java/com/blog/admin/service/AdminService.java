package com.blog.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.admin.entity.Admin;

import java.util.Map;

public interface AdminService extends IService<Admin> {

    /**
     * 管理员登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    Map<String, Object> login(String username, String password);

    /**
     * 生成JWT令牌
     * @param admin 管理员信息
     * @return JWT令牌
     */
    String generateToken(Admin admin);

    /**
     * 根据用户名获取管理员信息
     * @param username 用户名
     * @return 管理员信息
     */
    Admin getAdminByUsername(String username);

    /**
     * 获取管理员列表
     * @param page 页码
     * @param size 每页数量
     * @return 管理员列表
     */
    Page<Admin> getAdminList(int page, int size);

    /**
     * 更新管理员状态
     * @param id 管理员ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateAdminStatus(Long id, Integer status);

    /**
     * 重置管理员密码
     * @param id 管理员ID
     * @param newPassword 新密码
     * @return 重置结果
     */
    boolean resetAdminPassword(Long id, String newPassword);
}

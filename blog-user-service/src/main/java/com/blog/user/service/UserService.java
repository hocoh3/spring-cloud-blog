package com.blog.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.user.entity.User;
import java.util.Map;

public interface UserService extends IService<User> {
    
    User getUserByUsername(String username);
    
    User getUserByEmail(String email);
    
    boolean register(User user);
    
    boolean updateUserInfo(User user);
    
    /**
     * 用户登录，验证用户名和密码
     * @param username 用户名
     * @param password 密码
     * @return 用户信息，如果验证失败返回null
     */
    User login(String username, String password);
    
    /**
     * 生成JWT令牌
     * @param user 用户信息
     * @return JWT令牌
     */
    String generateToken(User user);
    
    /**
     * 从JWT令牌中获取用户信息
     * @param token JWT令牌
     * @return 用户信息映射
     */
    Map<String, Object> getUserInfoFromToken(String token);
}

package com.blog.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users")
    String getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size);

    @GetMapping("/users/{id}")
    String getUserById(@PathVariable Long id);

    @PutMapping("/users/{id}/status")
    String updateUserStatus(@PathVariable Long id, @RequestParam Integer status);
}
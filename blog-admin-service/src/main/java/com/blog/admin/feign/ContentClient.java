package com.blog.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "content-service")
public interface ContentClient {

    @GetMapping("/articles")
    String getArticleCount();

    @GetMapping("/articles/statistics")
    String getArticleStatistics();

    // 分类相关接口
    @GetMapping("/categories/page")
    String getCategoryList(
            @RequestParam(defaultValue = "1") Integer page, 
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword);

    @PostMapping("/categories")
    String createCategory(@RequestBody Object category);

    @PutMapping("/categories/{id}")
    String updateCategory(@PathVariable Long id, @RequestBody Object category);

    @DeleteMapping("/categories/{id}")
    String deleteCategory(@PathVariable Long id);
}

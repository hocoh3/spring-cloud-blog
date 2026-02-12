package com.blog.search.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "blog-content-service", path = "/articles")
public interface ContentClient {

    @GetMapping("/list")
    Map<String, Object> getArticleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "1000") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "2") Integer status
    );
}
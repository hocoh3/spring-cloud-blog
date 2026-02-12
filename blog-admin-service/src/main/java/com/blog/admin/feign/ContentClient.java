package com.blog.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "content-service")
public interface ContentClient {

    @GetMapping("/articles")
    String getArticleCount();

    @GetMapping("/articles/statistics")
    String getArticleStatistics();
}

package com.blog.content.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "data-service")
public interface DataClient {

    @PostMapping("/aggregation/article/{articleId}/view")
    Boolean incrementArticleView(@PathVariable("articleId") Long articleId);

    @PostMapping("/aggregation/article/{articleId}/like")
    Boolean incrementArticleLike(@PathVariable("articleId") Long articleId);

    @PostMapping("/aggregation/article/{articleId}/comment")
    Boolean incrementArticleComment(@PathVariable("articleId") Long articleId);

    @GetMapping("/aggregation/hot-articles")
    Map<String, Object> getHotArticles(@RequestParam(value = "limit", defaultValue = "10") int limit);
}

package com.blog.content.feign;

import com.blog.content.dto.SearchArticleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "blog-search-service", path = "/articles")
public interface SearchClient {

    @PostMapping("/index")
    Boolean indexArticle(@RequestBody SearchArticleDTO article);

    @PutMapping("/index")
    Boolean updateArticleIndex(@RequestBody SearchArticleDTO article);

    @DeleteMapping("/{articleId}")
    Boolean deleteArticleIndex(@PathVariable Long articleId);
}

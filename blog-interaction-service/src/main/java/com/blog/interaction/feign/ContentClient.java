package com.blog.interaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "content-service")
public interface ContentClient {

    @PostMapping("/articles/{id}/comment-count")
    Boolean updateCommentCount(@PathVariable("id") Long id, @RequestBody Integer count);

    @PostMapping("/articles/{id}/like-count")
    Boolean updateLikeCount(@PathVariable("id") Long id, @RequestBody Integer count);

    @GetMapping("/articles/{id}")
    ArticleDTO getArticleById(@PathVariable("id") Long id);

    class ArticleDTO {
        private Long id;
        private String title;
        private Long authorId;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Long getAuthorId() {
            return authorId;
        }

        public void setAuthorId(Long authorId) {
            this.authorId = authorId;
        }
    }
}

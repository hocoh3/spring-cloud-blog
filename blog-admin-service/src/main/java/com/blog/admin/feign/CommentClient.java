package com.blog.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "interaction-service")
public interface CommentClient {

    @GetMapping("/comments/count")
    String getCommentCount();

    @GetMapping("/comments/all")
    String getAllComments(@RequestParam("page") Integer page, @RequestParam("size") Integer size);

    @PutMapping("/comments/{id}/status")
    String updateCommentStatus(@PathVariable("id") Long id, @RequestParam("status") Integer status);

    @DeleteMapping("/comments/{id}")
    String deleteComment(@PathVariable("id") Long id);
}

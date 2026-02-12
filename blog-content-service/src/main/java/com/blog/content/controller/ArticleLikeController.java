package com.blog.content.controller;

import com.blog.content.entity.Article;
import com.blog.content.entity.ArticleLike;
import com.blog.content.service.ArticleLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article-likes")
public class ArticleLikeController {

    @Autowired
    private ArticleLikeService articleLikeService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> likeArticle(@RequestBody Map<String, Long> request) {
        Long articleId = request.get("articleId");
        Long userId = request.get("userId");

        boolean success = articleLikeService.likeArticle(articleId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("liked", success);
        response.put("likeCount", articleLikeService.getLikeCount(articleId));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> unlikeArticle(@RequestBody Map<String, Long> request) {
        Long articleId = request.get("articleId");
        Long userId = request.get("userId");

        boolean success = articleLikeService.unlikeArticle(articleId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("liked", !success);
        response.put("likeCount", articleLikeService.getLikeCount(articleId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkLike(
            @RequestParam Long articleId,
            @RequestParam Long userId) {
        boolean liked = articleLikeService.isLiked(articleId, userId);
        int likeCount = articleLikeService.getLikeCount(articleId);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Article>> getUserLikedArticles(@PathVariable Long userId) {
        List<Article> articles = articleLikeService.getUserLikedArticles(userId);
        return ResponseEntity.ok(articles);
    }
}
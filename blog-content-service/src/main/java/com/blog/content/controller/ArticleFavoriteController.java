package com.blog.content.controller;

import com.blog.content.entity.Article;
import com.blog.content.entity.ArticleFavorite;
import com.blog.content.service.ArticleFavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/article-favorites")
public class ArticleFavoriteController {

    @Autowired
    private ArticleFavoriteService articleFavoriteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> favoriteArticle(@RequestBody Map<String, Long> request) {
        Long articleId = request.get("articleId");
        Long userId = request.get("userId");

        boolean success = articleFavoriteService.favoriteArticle(articleId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("favorited", success);
        response.put("favoriteCount", articleFavoriteService.getFavoriteCount(articleId));

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> unfavoriteArticle(@RequestBody Map<String, Long> request) {
        Long articleId = request.get("articleId");
        Long userId = request.get("userId");

        boolean success = articleFavoriteService.unfavoriteArticle(articleId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("favorited", !success);
        response.put("favoriteCount", articleFavoriteService.getFavoriteCount(articleId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFavorite(
            @RequestParam Long articleId,
            @RequestParam Long userId) {
        boolean favorited = articleFavoriteService.isFavorited(articleId, userId);
        int favoriteCount = articleFavoriteService.getFavoriteCount(articleId);

        Map<String, Object> response = new HashMap<>();
        response.put("favorited", favorited);
        response.put("favoriteCount", favoriteCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Article>> getUserFavoriteArticles(@PathVariable Long userId) {
        List<Article> articles = articleFavoriteService.getUserFavoriteArticles(userId);
        return ResponseEntity.ok(articles);
    }
}
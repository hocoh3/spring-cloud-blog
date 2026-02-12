package com.blog.data.controller;

import com.blog.data.service.AggregationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/aggregation")
public class AggregationController {

    @Autowired
    private AggregationService aggregationService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<Map<String, Object>> getArticleStats(@PathVariable Long articleId) {
        Map<String, Object> stats = aggregationService.getArticleStats(articleId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        Map<String, Object> stats = aggregationService.getUserStats(userId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/hot-articles")
    public ResponseEntity<Map<String, Object>> getHotArticles(
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = aggregationService.getHotArticles(limit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/active-users")
    public ResponseEntity<Map<String, Object>> getActiveUsers(
            @RequestParam(defaultValue = "10") int limit) {
        Map<String, Object> result = aggregationService.getActiveUsers(limit);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/article/{articleId}/view")
    public ResponseEntity<Boolean> incrementArticleView(@PathVariable Long articleId) {
        boolean result = aggregationService.incrementArticleView(articleId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/article/{articleId}/like")
    public ResponseEntity<Boolean> incrementArticleLike(@PathVariable Long articleId) {
        boolean result = aggregationService.incrementArticleLike(articleId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/article/{articleId}/comment")
    public ResponseEntity<Boolean> incrementArticleComment(@PathVariable Long articleId) {
        boolean result = aggregationService.incrementArticleComment(articleId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/{userId}/activity")
    public ResponseEntity<Boolean> updateUserActivity(@PathVariable Long userId) {
        boolean result = aggregationService.updateUserActivity(userId);
        return ResponseEntity.ok(result);
    }
}

package com.blog.content.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.content.dto.ArticleDTO;
import com.blog.content.entity.Article;
import com.blog.content.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // 获取文章列表（分页）- 默认端点
    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "createTime") String sortBy) {
        Page<ArticleDTO> articlePage = articleService.getArticleListWithDetails(page, size, categoryId, status, sortBy);
        return ResponseEntity.ok(articlePage);
    }

    // 获取文章列表（分页）
    @GetMapping("/list")
    public ResponseEntity<Page<ArticleDTO>> getArticleList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "createTime") String sortBy) {
        Page<ArticleDTO> articlePage = articleService.getArticleListWithDetails(page, size, categoryId, status, sortBy);
        return ResponseEntity.ok(articlePage);
    }

    // 根据ID获取文章详情
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleDetail(@PathVariable Long id) {
        ArticleDTO article = articleService.getArticleDetailWithDetails(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        // 增加阅读量
        articleService.increaseViewCount(id);
        return ResponseEntity.ok(article);
    }

    // 根据用户ID获取文章列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ArticleDTO>> getArticlesByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<ArticleDTO> articlePage = articleService.getArticlesByUserIdWithDetails(userId, page, size);
        return ResponseEntity.ok(articlePage);
    }

    // 创建文章
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        article.setStatus(0);
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        articleService.save(article);
        return ResponseEntity.ok(article);
    }

    // 更新文章
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        article.setUpdateTime(LocalDateTime.now());
        articleService.updateById(article);
        return ResponseEntity.ok(article);
    }

    // 删除文章
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteArticle(@PathVariable Long id) {
        boolean result = articleService.deleteArticle(id);
        return ResponseEntity.ok(result);
    }

    // 增加点赞数
    @PostMapping("/{id}/like")
    public ResponseEntity<Boolean> likeArticle(@PathVariable Long id) {
        boolean result = articleService.increaseLikeCount(id);
        return ResponseEntity.ok(result);
    }

    // 减少点赞数
    @PostMapping("/{id}/unlike")
    public ResponseEntity<Boolean> unlikeArticle(@PathVariable Long id) {
        boolean result = articleService.decreaseLikeCount(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/comment-count")
    public ResponseEntity<Boolean> updateCommentCount(@PathVariable Long id, @RequestBody Integer count) {
        boolean result = articleService.updateCommentCount(id, count);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/like-count")
    public ResponseEntity<Boolean> updateLikeCount(@PathVariable Long id, @RequestBody Integer count) {
        boolean result = articleService.updateLikeCount(id, count);
        return ResponseEntity.ok(result);
    }

    // 获取热门文章
    @GetMapping("/hot")
    public ResponseEntity<Page<ArticleDTO>> getHotArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "5") Integer size) {
        Page<ArticleDTO> articlePage = articleService.getHotArticlesWithDetails(page, size);
        return ResponseEntity.ok(articlePage);
    }

    // 批量同步文章到搜索引擎
    @PostMapping("/sync/search")
    public ResponseEntity<String> syncAllToSearchEngine() {
        boolean result = articleService.syncAllToSearchEngine();
        if (result) {
            return ResponseEntity.ok("文章同步到搜索引擎成功");
        } else {
            return ResponseEntity.internalServerError().body("文章同步到搜索引擎失败");
        }
    }

    // 发布文章
    @PostMapping("/{id}/publish")
    public ResponseEntity<String> publishArticle(@PathVariable Long id) {
        boolean result = articleService.publishArticle(id);
        if (result) {
            return ResponseEntity.ok("文章发布成功");
        } else {
            return ResponseEntity.internalServerError().body("文章发布失败");
        }
    }

    // 撤回发布文章
    @PostMapping("/{id}/unpublish")
    public ResponseEntity<String> unpublishArticle(@PathVariable Long id) {
        boolean result = articleService.unpublishArticle(id);
        if (result) {
            return ResponseEntity.ok("文章撤回发布成功");
        } else {
            return ResponseEntity.internalServerError().body("文章撤回发布失败");
        }
    }

    // 获取文章统计数据（用于统计）
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getArticleStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalViews", articleService.getTotalViewCount());
        return ResponseEntity.ok(statistics);
    }

    @PostMapping("/{id}/submit-review")
    public ResponseEntity<String> submitForReview(@PathVariable Long id) {
        boolean result = articleService.submitForReview(id);
        if (result) {
            return ResponseEntity.ok("文章提交审核成功");
        } else {
            return ResponseEntity.internalServerError().body("文章提交审核失败");
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveArticle(@PathVariable Long id, @RequestBody(required = false) Map<String, String> params) {
        String reviewComment = params != null ? params.get("reviewComment") : null;
        boolean result = articleService.approveArticle(id, reviewComment);
        if (result) {
            return ResponseEntity.ok("文章审核通过成功");
        } else {
            return ResponseEntity.internalServerError().body("文章审核通过失败");
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectArticle(@PathVariable Long id, @RequestBody(required = false) Map<String, String> params) {
        String reviewComment = params != null ? params.get("reviewComment") : null;
        boolean result = articleService.rejectArticle(id, reviewComment);
        if (result) {
            return ResponseEntity.ok("文章审核拒绝成功");
        } else {
            return ResponseEntity.internalServerError().body("文章审核拒绝失败");
        }
    }

    @GetMapping("/pending-review")
    public ResponseEntity<Page<Article>> getPendingReviewArticles(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Article> articlePage = articleService.getPendingReviewArticles(page, size);
        return ResponseEntity.ok(articlePage);
    }

    @PutMapping("/{id}/top")
    public ResponseEntity<String> toggleArticleTop(@PathVariable Long id, @RequestParam Integer isTop) {
        boolean result = articleService.toggleArticleTop(id, isTop);
        if (result) {
            return ResponseEntity.ok(isTop == 1 ? "文章置顶成功" : "取消文章置顶成功");
        } else {
            return ResponseEntity.internalServerError().body("操作失败");
        }
    }
}

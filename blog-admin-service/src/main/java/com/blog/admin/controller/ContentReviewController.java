package com.blog.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.admin.entity.ContentReview;
import com.blog.admin.service.ContentReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/review")
public class ContentReviewController {

    @Autowired
    private ContentReviewService contentReviewService;

    /**
     * 获取内容审核列表
     */
    @GetMapping("/list")
    public ResponseEntity<Page<ContentReview>> getContentReviewList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer reviewStatus,
            @RequestParam(required = false) String keyword) {
        Page<ContentReview> reviewPage = contentReviewService.getContentReviewList(page, size, reviewStatus, keyword);
        return ResponseEntity.ok(reviewPage);
    }

    /**
     * 根据ID获取审核内容详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<ContentReview> getContentReviewById(@PathVariable Long id) {
        ContentReview contentReview = contentReviewService.getById(id);
        if (contentReview == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(contentReview);
    }

    /**
     * 审核通过
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<Boolean> approveReview(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long adminId = Long.valueOf(request.get("adminId").toString());
        String adminName = request.get("adminName").toString();
        String comment = request.getOrDefault("comment", "").toString();

        boolean result = contentReviewService.approveReview(id, adminId, adminName, comment);
        return ResponseEntity.ok(result);
    }

    /**
     * 审核拒绝
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<Boolean> rejectReview(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Long adminId = Long.valueOf(request.get("adminId").toString());
        String adminName = request.get("adminName").toString();
        String comment = request.getOrDefault("comment", "").toString();

        boolean result = contentReviewService.rejectReview(id, adminId, adminName, comment);
        return ResponseEntity.ok(result);
    }
}
package com.blog.interaction.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.interaction.entity.Comment;
import com.blog.interaction.service.CommentService;
import com.blog.interaction.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<Page<CommentVO>> getCommentsByArticleId(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CommentVO> commentPage = commentService.getCommentsByArticleId(articleId, page, size);
        return ResponseEntity.ok(commentPage);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CommentVO>> getCommentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<CommentVO> commentPage = commentService.getCommentsByUserId(userId, page, size);
        return ResponseEntity.ok(commentPage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
        Comment comment = commentService.getById(id);
        if (comment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comment);
    }

    @PostMapping
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
        boolean result = commentService.addComment(comment);
        if (!result) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long id, @RequestBody Comment comment) {
        comment.setId(id);
        commentService.updateById(comment);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteComment(@PathVariable Long id) {
        boolean result = commentService.deleteComment(id);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Boolean> updateCommentStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean result = commentService.updateCommentStatus(id, status);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Comment>> getAllComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<Comment> commentPage = commentService.page(new Page<>(page, size));
        return ResponseEntity.ok(commentPage);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getCommentCount() {
        Map<String, Long> result = new HashMap<>();
        result.put("total", commentService.count());
        return ResponseEntity.ok(result);
    }
}

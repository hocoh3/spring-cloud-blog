package com.blog.content.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.content.entity.Article;
import com.blog.content.entity.Category;
import com.blog.content.service.ArticleService;
import com.blog.content.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleService articleService;

    // 获取分类列表（带文章数量）
    @GetMapping
    public ResponseEntity<List<Category>> getCategoryList() {
        List<Category> categories = categoryService.list();
        for (Category category : categories) {
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Article::getCategoryId, category.getId())
                    .eq(Article::getStatus, 2);
            long count = articleService.count(queryWrapper);
            category.setArticleCount((int) count);
        }
        return ResponseEntity.ok(categories);
    }

    // 获取分类列表（分页）
    @GetMapping("/page")
    public ResponseEntity<Page<Category>> getCategoryPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<Category> categoryPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.like(Category::getName, keyword);
            categoryPage = categoryService.page(new Page<>(page, size), queryWrapper);
        } else {
            categoryPage = categoryService.page(new Page<>(page, size));
        }
        for (Category category : categoryPage.getRecords()) {
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Article::getCategoryId, category.getId())
                    .eq(Article::getStatus, 2);
            long count = articleService.count(queryWrapper);
            category.setArticleCount((int) count);
        }
        return ResponseEntity.ok(categoryPage);
    }

    // 根据ID获取分类
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    // 创建分类
    @PostMapping
    public ResponseEntity<Boolean> createCategory(@RequestBody Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        boolean result = categoryService.save(category);
        return ResponseEntity.ok(result);
    }

    // 更新分类
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        category.setUpdateTime(LocalDateTime.now());
        categoryService.updateById(category);
        return ResponseEntity.ok(category);
    }

    // 删除分类
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCategory(@PathVariable Long id) {
        boolean result = categoryService.removeById(id);
        return ResponseEntity.ok(result);
    }
}

package com.blog.search.controller;

import com.blog.search.entity.SearchArticle;
import com.blog.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 全文搜索文章
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 搜索结果
     */
    @GetMapping("")
    public Page<SearchArticle> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return searchService.searchArticles(keyword, pageable);
    }

    /**
     * 按分类搜索文章
     * @param keyword 搜索关键词
     * @param categoryId 分类ID
     * @param page 页码
     * @param size 每页数量
     * @return 搜索结果
     */
    @GetMapping("/category")
    public Page<SearchArticle> searchArticlesByCategory(
            @RequestParam String keyword,
            @RequestParam Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return searchService.searchArticlesByCategory(keyword, categoryId, pageable);
    }

    /**
     * 索引单篇文章
     * @param article 文章对象
     * @return 是否索引成功
     */
    @PostMapping("/index")
    public boolean indexArticle(@RequestBody SearchArticle article) {
        return searchService.indexArticle(article);
    }

    /**
     * 更新索引中的文章
     * @param article 文章对象
     * @return 是否更新成功
     */
    @PutMapping("/index")
    public boolean updateArticleIndex(@RequestBody SearchArticle article) {
        return searchService.updateArticleIndex(article);
    }

    /**
     * 删除索引中的文章
     * @param articleId 文章ID
     * @return 是否删除成功
     */
    @DeleteMapping("/{articleId}")
    public boolean deleteArticleIndex(@PathVariable Long articleId) {
        return searchService.deleteArticleIndex(articleId);
    }

    /**
     * 批量同步所有已发布的文章到搜索引擎
     * @return 同步结果
     */
    @PostMapping("/sync-all")
    public String syncAllArticles() {
        return searchService.syncAllArticles();
    }
}
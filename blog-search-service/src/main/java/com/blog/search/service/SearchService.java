package com.blog.search.service;

import com.blog.search.entity.SearchArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    /**
     * 全文搜索文章
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<SearchArticle> searchArticles(String keyword, Pageable pageable);

    /**
     * 按分类搜索文章
     * @param keyword 搜索关键词
     * @param categoryId 分类ID
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<SearchArticle> searchArticlesByCategory(String keyword, Long categoryId, Pageable pageable);

    /**
     * 索引单篇文章
     * @param article 文章对象
     * @return 是否索引成功
     */
    boolean indexArticle(SearchArticle article);

    /**
     * 更新索引中的文章
     * @param article 文章对象
     * @return 是否更新成功
     */
    boolean updateArticleIndex(SearchArticle article);

    /**
     * 删除索引中的文章
     * @param articleId 文章ID
     * @return 是否删除成功
     */
    boolean deleteArticleIndex(Long articleId);

    /**
     * 批量同步所有已发布的文章到搜索引擎
     * @return 同步结果
     */
    String syncAllArticles();
}
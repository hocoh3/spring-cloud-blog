package com.blog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.content.entity.Article;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.content.dto.ArticleDTO;

public interface ArticleService extends IService<Article> {

    Page<Article> getArticleList(Integer page, Integer size, Long categoryId, Integer status);

    Page<Article> getArticleList(Integer page, Integer size, Long categoryId, Integer status, String sortBy);

    Page<ArticleDTO> getArticleListWithDetails(Integer page, Integer size, Long categoryId, Integer status);

    Page<ArticleDTO> getArticleListWithDetails(Integer page, Integer size, Long categoryId, Integer status, String sortBy);

    Page<Article> getArticlesByUserId(Long userId, Integer page, Integer size);

    Page<ArticleDTO> getArticlesByUserIdWithDetails(Long userId, Integer page, Integer size);

    Page<Article> getHotArticles(Integer page, Integer size);

    Page<ArticleDTO> getHotArticlesWithDetails(Integer page, Integer size);

    Article getArticleDetail(Long id);

    ArticleDTO getArticleDetailWithDetails(Long id);

    boolean increaseViewCount(Long id);

    boolean increaseCommentCount(Long id);

    boolean increaseLikeCount(Long id);

    boolean decreaseLikeCount(Long id);

    boolean updateCommentCount(Long id, Integer count);

    boolean updateLikeCount(Long id, Integer count);

    boolean syncToSearchEngine(Article article);

    boolean syncAllToSearchEngine();

    boolean publishArticle(Long id);

    boolean unpublishArticle(Long id);

    int getTotalViewCount();

    boolean submitForReview(Long id);

    boolean approveArticle(Long id, String reviewComment);

    boolean rejectArticle(Long id, String reviewComment);

    Page<Article> getPendingReviewArticles(Integer page, Integer size);

    boolean deleteArticle(Long id);

    boolean toggleArticleTop(Long id, Integer isTop);
}

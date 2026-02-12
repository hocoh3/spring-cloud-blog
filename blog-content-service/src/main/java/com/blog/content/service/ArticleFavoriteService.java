package com.blog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.content.entity.Article;
import com.blog.content.entity.ArticleFavorite;

import java.util.List;

public interface ArticleFavoriteService extends IService<ArticleFavorite> {

    boolean favoriteArticle(Long articleId, Long userId);

    boolean unfavoriteArticle(Long articleId, Long userId);

    boolean isFavorited(Long articleId, Long userId);

    int getFavoriteCount(Long articleId);

    List<Article> getUserFavoriteArticles(Long userId);
}
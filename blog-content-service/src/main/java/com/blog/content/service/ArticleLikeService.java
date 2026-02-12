package com.blog.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.content.entity.Article;
import com.blog.content.entity.ArticleLike;

import java.util.List;

public interface ArticleLikeService extends IService<ArticleLike> {

    boolean likeArticle(Long articleId, Long userId);

    boolean unlikeArticle(Long articleId, Long userId);

    boolean isLiked(Long articleId, Long userId);

    int getLikeCount(Long articleId);

    List<Article> getUserLikedArticles(Long userId);
}
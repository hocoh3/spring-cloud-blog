package com.blog.data.service;

import java.util.Map;

public interface AggregationService {

    Map<String, Object> getArticleStats(Long articleId);

    Map<String, Object> getUserStats(Long userId);

    Map<String, Object> getHotArticles(int limit);

    Map<String, Object> getActiveUsers(int limit);

    boolean incrementArticleView(Long articleId);

    boolean incrementArticleLike(Long articleId);

    boolean incrementArticleComment(Long articleId);

    boolean updateUserActivity(Long userId);
}

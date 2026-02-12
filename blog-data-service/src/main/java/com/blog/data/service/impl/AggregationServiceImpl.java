package com.blog.data.service.impl;

import com.blog.data.service.AggregationService;
import com.blog.data.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AggregationServiceImpl implements AggregationService {

    @Autowired
    private RedisUtil redisUtil;

    private static final String ARTICLE_VIEW_PREFIX = "article:view:";
    private static final String ARTICLE_LIKE_PREFIX = "article:like:";
    private static final String ARTICLE_COMMENT_PREFIX = "article:comment:";
    private static final String USER_ACTIVITY_PREFIX = "user:activity:";
    private static final String HOT_ARTICLES_KEY = "ranking:hot:articles";
    private static final String ACTIVE_USERS_KEY = "ranking:active:users";

    @Override
    public Map<String, Object> getArticleStats(Long articleId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            String viewKey = ARTICLE_VIEW_PREFIX + articleId;
            String likeKey = ARTICLE_LIKE_PREFIX + articleId;
            String commentKey = ARTICLE_COMMENT_PREFIX + articleId;

            Object viewObj = redisUtil.get(viewKey);
            Object likeObj = redisUtil.get(likeKey);
            Object commentObj = redisUtil.get(commentKey);

            long viewCount = parseLong(viewObj);
            long likeCount = parseLong(likeObj);
            long commentCount = parseLong(commentObj);

            stats.put("articleId", articleId);
            stats.put("viewCount", viewCount);
            stats.put("likeCount", likeCount);
            stats.put("commentCount", commentCount);

            return stats;
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("articleId", articleId);
            errorStats.put("viewCount", 0);
            errorStats.put("likeCount", 0);
            errorStats.put("commentCount", 0);
            errorStats.put("error", e.getMessage());
            return errorStats;
        }
    }

    private long parseLong(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        if (obj instanceof byte[]) {
            try {
                return Long.parseLong(new String((byte[]) obj));
            } catch (Exception e) {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public Map<String, Object> getUserStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        String activityKey = USER_ACTIVITY_PREFIX + userId;

        Object activityObj = redisUtil.get(activityKey);
        long activityScore = 0;
        
        if (activityObj != null) {
            if (activityObj instanceof Number) {
                activityScore = ((Number) activityObj).longValue();
            } else if (activityObj instanceof String) {
                try {
                    activityScore = Long.parseLong((String) activityObj);
                } catch (NumberFormatException e) {
                }
            }
        }
        
        stats.put("userId", userId);
        stats.put("activityScore", activityScore);

        return stats;
    }

    @Override
    public Map<String, Object> getHotArticles(int limit) {
        Map<String, Object> result = new HashMap<>();
        Set<Object> hotArticles = redisUtil.zReverseRange(HOT_ARTICLES_KEY, 0, limit - 1);
        result.put("hotArticles", hotArticles);
        result.put("count", hotArticles != null ? hotArticles.size() : 0);
        return result;
    }

    @Override
    public Map<String, Object> getActiveUsers(int limit) {
        Map<String, Object> result = new HashMap<>();
        Set<Object> activeUsers = redisUtil.zReverseRange(ACTIVE_USERS_KEY, 0, limit - 1);
        result.put("activeUsers", activeUsers);
        result.put("count", activeUsers != null ? activeUsers.size() : 0);
        return result;
    }

    @Override
    public boolean incrementArticleView(Long articleId) {
        try {
            String viewKey = ARTICLE_VIEW_PREFIX + articleId;
            long viewCount = redisUtil.increment(viewKey);
            
            Object likeObj = redisUtil.get(ARTICLE_LIKE_PREFIX + articleId);
            Object commentObj = redisUtil.get(ARTICLE_COMMENT_PREFIX + articleId);
            
            long likeCount = 0;
            long commentCount = 0;
            
            if (likeObj != null && likeObj instanceof Number) {
                likeCount = ((Number) likeObj).longValue();
            }
            if (commentObj != null && commentObj instanceof Number) {
                commentCount = ((Number) commentObj).longValue();
            }
            
            long totalScore = viewCount + likeCount + commentCount;
            redisUtil.zAdd(HOT_ARTICLES_KEY, articleId.toString(), totalScore);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean incrementArticleLike(Long articleId) {
        try {
            String likeKey = ARTICLE_LIKE_PREFIX + articleId;
            long likeCount = redisUtil.increment(likeKey);
            
            Object viewObj = redisUtil.get(ARTICLE_VIEW_PREFIX + articleId);
            Object commentObj = redisUtil.get(ARTICLE_COMMENT_PREFIX + articleId);
            
            long viewCount = 0;
            long commentCount = 0;
            
            if (viewObj != null && viewObj instanceof Number) {
                viewCount = ((Number) viewObj).longValue();
            }
            if (commentObj != null && commentObj instanceof Number) {
                commentCount = ((Number) commentObj).longValue();
            }
            
            long totalScore = viewCount + likeCount + commentCount;
            redisUtil.zAdd(HOT_ARTICLES_KEY, articleId.toString(), totalScore);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean incrementArticleComment(Long articleId) {
        try {
            String commentKey = ARTICLE_COMMENT_PREFIX + articleId;
            long commentCount = redisUtil.increment(commentKey);
            
            Object viewObj = redisUtil.get(ARTICLE_VIEW_PREFIX + articleId);
            Object likeObj = redisUtil.get(ARTICLE_LIKE_PREFIX + articleId);
            
            long viewCount = 0;
            long likeCount = 0;
            
            if (viewObj != null && viewObj instanceof Number) {
                viewCount = ((Number) viewObj).longValue();
            }
            if (likeObj != null && likeObj instanceof Number) {
                likeCount = ((Number) likeObj).longValue();
            }
            
            long totalScore = viewCount + likeCount + commentCount;
            redisUtil.zAdd(HOT_ARTICLES_KEY, articleId.toString(), totalScore);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateUserActivity(Long userId) {
        try {
            String activityKey = USER_ACTIVITY_PREFIX + userId;
            long activityScore = redisUtil.increment(activityKey);
            
            redisUtil.zAdd(ACTIVE_USERS_KEY, userId.toString(), activityScore);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

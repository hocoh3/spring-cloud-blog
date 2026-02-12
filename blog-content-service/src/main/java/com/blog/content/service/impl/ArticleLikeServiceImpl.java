package com.blog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.content.entity.Article;
import com.blog.content.entity.ArticleLike;
import com.blog.content.mapper.ArticleLikeMapper;
import com.blog.content.service.ArticleLikeService;
import com.blog.content.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleLikeServiceImpl extends ServiceImpl<ArticleLikeMapper, ArticleLike> implements ArticleLikeService {

    @Autowired
    private ArticleService articleService;

    @Override
    @Transactional
    public boolean likeArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLike::getArticleId, articleId)
                .eq(ArticleLike::getUserId, userId);

        ArticleLike existingLike = getOne(queryWrapper);
        if (existingLike != null) {
            return false;
        }

        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticleId(articleId);
        articleLike.setUserId(userId);
        boolean saved = save(articleLike);

        if (saved) {
            articleService.increaseLikeCount(articleId);
        }

        return saved;
    }

    @Override
    @Transactional
    public boolean unlikeArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLike::getArticleId, articleId)
                .eq(ArticleLike::getUserId, userId);

        boolean removed = remove(queryWrapper);

        if (removed) {
            articleService.decreaseLikeCount(articleId);
        }

        return removed;
    }

    @Override
    public boolean isLiked(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLike::getArticleId, articleId)
                .eq(ArticleLike::getUserId, userId);

        return count(queryWrapper) > 0;
    }

    @Override
    public int getLikeCount(Long articleId) {
        LambdaQueryWrapper<ArticleLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLike::getArticleId, articleId);

        return Math.toIntExact(count(queryWrapper));
    }

    @Override
    public List<Article> getUserLikedArticles(Long userId) {
        LambdaQueryWrapper<ArticleLike> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleLike::getUserId, userId);
        
        List<ArticleLike> articleLikes = list(queryWrapper);
        
        if (articleLikes.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> articleIds = articleLikes.stream()
                .map(ArticleLike::getArticleId)
                .collect(Collectors.toList());
        
        return articleService.listByIds(articleIds);
    }
}
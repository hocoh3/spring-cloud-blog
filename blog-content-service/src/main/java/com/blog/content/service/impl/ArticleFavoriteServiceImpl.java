package com.blog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.content.entity.Article;
import com.blog.content.entity.ArticleFavorite;
import com.blog.content.mapper.ArticleFavoriteMapper;
import com.blog.content.service.ArticleFavoriteService;
import com.blog.content.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleFavoriteServiceImpl extends ServiceImpl<ArticleFavoriteMapper, ArticleFavorite> implements ArticleFavoriteService {

    @Autowired
    private ArticleService articleService;

    @Override
    @Transactional
    public boolean favoriteArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getArticleId, articleId)
                .eq(ArticleFavorite::getUserId, userId);

        ArticleFavorite existingFavorite = getOne(queryWrapper);
        if (existingFavorite != null) {
            return false;
        }

        ArticleFavorite articleFavorite = new ArticleFavorite();
        articleFavorite.setArticleId(articleId);
        articleFavorite.setUserId(userId);

        return save(articleFavorite);
    }

    @Override
    @Transactional
    public boolean unfavoriteArticle(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getArticleId, articleId)
                .eq(ArticleFavorite::getUserId, userId);

        return remove(queryWrapper);
    }

    @Override
    public boolean isFavorited(Long articleId, Long userId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getArticleId, articleId)
                .eq(ArticleFavorite::getUserId, userId);

        return count(queryWrapper) > 0;
    }

    @Override
    public int getFavoriteCount(Long articleId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getArticleId, articleId);

        return Math.toIntExact(count(queryWrapper));
    }

    @Override
    public List<Article> getUserFavoriteArticles(Long userId) {
        LambdaQueryWrapper<ArticleFavorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleFavorite::getUserId, userId);
        
        List<ArticleFavorite> articleFavorites = list(queryWrapper);
        
        if (articleFavorites.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<Long> articleIds = articleFavorites.stream()
                .map(ArticleFavorite::getArticleId)
                .collect(Collectors.toList());
        
        return articleService.listByIds(articleIds);
    }
}
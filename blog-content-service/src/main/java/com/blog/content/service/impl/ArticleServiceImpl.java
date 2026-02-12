package com.blog.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.content.dto.SearchArticleDTO;
import com.blog.content.entity.Article;
import com.blog.content.feign.DataClient;
import com.blog.content.feign.InteractionClient;
import com.blog.content.feign.SearchClient;
import com.blog.content.mapper.ArticleMapper;
import com.blog.content.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private final SearchClient searchClient;
    private final InteractionClient interactionClient;
    private final DataClient dataClient;

    @Override
    public Page<Article> getArticleList(Integer page, Integer size, Long categoryId, Integer status) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            queryWrapper.eq(Article::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Article::getIsTop, Article::getCreateTime);

        if (categoryId != null) {
            queryWrapper.eq(Article::getCategoryId, categoryId);
        }

        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Page<Article> getArticlesByUserId(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getUserId, userId)
                .orderByDesc(Article::getCreateTime);

        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Page<Article> getHotArticles(Integer page, Integer size) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, 2)
                .orderByDesc(Article::getViewCount, Article::getLikeCount);

        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Article getArticleDetail(Long id) {
        return getById(id);
    }

    @Override
    public boolean increaseViewCount(Long id) {
        boolean result = lambdaUpdate()
                .setSql("view_count = view_count + 1")
                .eq(Article::getId, id)
                .update();
        
        if (result) {
            try {
                dataClient.incrementArticleView(id);
            } catch (Exception e) {
                log.error("更新文章浏览量到Redis失败", e);
            }
        }
        
        return result;
    }

    @Override
    public boolean increaseCommentCount(Long id) {
        boolean result = lambdaUpdate()
                .setSql("comment_count = comment_count + 1")
                .eq(Article::getId, id)
                .update();
        
        if (result) {
            try {
                dataClient.incrementArticleComment(id);
            } catch (Exception e) {
                log.error("更新文章评论数到Redis失败", e);
            }
        }
        
        return result;
    }

    @Override
    public boolean increaseLikeCount(Long id) {
        boolean result = lambdaUpdate()
                .setSql("like_count = like_count + 1")
                .eq(Article::getId, id)
                .update();
        
        if (result) {
            try {
                dataClient.incrementArticleLike(id);
            } catch (Exception e) {
                log.error("更新文章点赞数到Redis失败", e);
            }
        }
        
        return result;
    }

    @Override
    public boolean decreaseLikeCount(Long id) {
        return lambdaUpdate()
                .setSql("like_count = like_count - 1")
                .eq(Article::getId, id)
                .update();
    }

    @Override
    public boolean updateCommentCount(Long id, Integer count) {
        return lambdaUpdate()
                .set(Article::getCommentCount, count)
                .eq(Article::getId, id)
                .update();
    }

    @Override
    public boolean updateLikeCount(Long id, Integer count) {
        return lambdaUpdate()
                .set(Article::getLikeCount, count)
                .eq(Article::getId, id)
                .update();
    }

    @Override
    public boolean syncToSearchEngine(Article article) {
        try {
            SearchArticleDTO searchArticleDTO = new SearchArticleDTO();
            searchArticleDTO.setId(article.getId());
            searchArticleDTO.setUserId(article.getUserId());
            searchArticleDTO.setTitle(article.getTitle());
            searchArticleDTO.setSummary(article.getSummary());
            searchArticleDTO.setContent(article.getContent());
            searchArticleDTO.setCategoryId(article.getCategoryId());
            searchArticleDTO.setCoverImage(article.getCoverImage());
            searchArticleDTO.setViewCount(article.getViewCount());
            searchArticleDTO.setCommentCount(article.getCommentCount());
            searchArticleDTO.setLikeCount(article.getLikeCount());
            searchArticleDTO.setStatus(article.getStatus());
            searchArticleDTO.setIsFeatured(article.getIsFeatured());
            searchArticleDTO.setIsTop(article.getIsTop());
            searchArticleDTO.setCreateTime(article.getCreateTime() != null ? article.getCreateTime().toString() : null);
            searchArticleDTO.setUpdateTime(article.getUpdateTime() != null ? article.getUpdateTime().toString() : null);
            
            log.info("准备同步文章到搜索引擎: id={}, title={}", article.getId(), article.getTitle());
            Boolean result = searchClient.indexArticle(searchArticleDTO);
            log.info("文章同步到搜索引擎结果: id={}, result={}", article.getId(), result);
            return result != null && result;
        } catch (Exception e) {
            log.error("文章同步到搜索引擎失败: {}", article.getId(), e);
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean syncAllToSearchEngine() {
        try {
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Article::getStatus, 2);
            List<Article> articles = list(queryWrapper);
            
            for (Article article : articles) {
                syncToSearchEngine(article);
            }
            
            log.info("批量同步文章到搜索引擎成功，共 {} 篇", articles.size());
            return true;
        } catch (Exception e) {
            log.error("批量同步文章到搜索引擎失败", e);
            return false;
        }
    }

    @Override
    public boolean publishArticle(Long id) {
        try {
            Article article = getById(id);
            if (article == null) {
                log.warn("文章不存在，无法发布: id={}", id);
                return false;
            }

            if (article.getStatus() == 2) {
                log.info("文章已经是发布状态: id={}", id);
                return true;
            }

            article.setStatus(2);
            article.setUpdateTime(LocalDateTime.now());
            boolean updateResult = updateById(article);

            if (updateResult) {
                boolean syncResult = syncToSearchEngine(article);
                if (syncResult) {
                    log.info("文章发布成功并已同步到搜索引擎: id={}, title={}", id, article.getTitle());
                    return true;
                } else {
                    log.error("文章发布成功但同步到搜索引擎失败: id={}", id);
                    return false;
                }
            } else {
                log.error("文章发布失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("发布文章时发生异常: id={}", id, e);
            return false;
        }
    }

    @Override
    public boolean unpublishArticle(Long id) {
        try {
            Article article = getById(id);
            if (article == null) {
                log.warn("文章不存在，无法撤回发布: id={}", id);
                return false;
            }

            if (article.getStatus() == 0) {
                log.info("文章已经是草稿状态: id={}", id);
                return true;
            }

            article.setStatus(0);
            article.setUpdateTime(LocalDateTime.now());
            boolean updateResult = updateById(article);

            if (updateResult) {
                try {
                    Boolean deleteResult = searchClient.deleteArticleIndex(id);
                    if (deleteResult != null && deleteResult) {
                        log.info("文章撤回发布成功并已从搜索引擎删除: id={}, title={}", id, article.getTitle());
                        return true;
                    } else {
                        log.warn("文章撤回发布成功但从搜索引擎删除失败: id={}", id);
                        return true;
                    }
                } catch (Exception e) {
                    log.error("文章撤回发布成功但从搜索引擎删除时发生异常: id={}", id, e);
                    return true;
                }
            } else {
                log.error("文章撤回发布失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("撤回文章发布时发生异常: id={}", id, e);
            return false;
        }
    }

    @Override
    public boolean submitForReview(Long id) {
        try {
            Article article = getById(id);
            if (article == null) {
                log.warn("文章不存在，无法提交审核: id={}", id);
                return false;
            }

            if (article.getStatus() != 0) {
                log.warn("只有草稿状态的文章才能提交审核，当前状态: id={}, status={}", id, article.getStatus());
                return false;
            }

            article.setStatus(1);
            article.setUpdateTime(LocalDateTime.now());
            boolean result = updateById(article);

            if (result) {
                log.info("文章提交审核成功: id={}, title={}", id, article.getTitle());
            } else {
                log.error("文章提交审核失败: id={}", id);
            }

            return result;
        } catch (Exception e) {
            log.error("提交文章审核时发生异常: id={}", id, e);
            return false;
        }
    }

    @Override
    public boolean approveArticle(Long id, String reviewComment) {
        try {
            Article article = getById(id);
            if (article == null) {
                log.warn("文章不存在，无法审核通过: id={}", id);
                return false;
            }

            if (article.getStatus() != 1) {
                log.warn("只有待审核状态的文章才能审核通过，当前状态: id={}, status={}", id, article.getStatus());
                return false;
            }

            article.setStatus(2);
            article.setUpdateTime(LocalDateTime.now());
            boolean updateResult = updateById(article);

            if (updateResult) {
                boolean syncResult = syncToSearchEngine(article);
                if (syncResult) {
                    log.info("文章审核通过并已同步到搜索引擎: id={}, title={}, 审核意见={}", id, article.getTitle(), reviewComment);
                    
                    if (article.getUserId() != null) {
                        Map<String, Object> notification = new HashMap<>();
                        notification.put("userId", article.getUserId());
                        notification.put("title", "您的文章已发布");
                        notification.put("content", "您的文章《" + article.getTitle() + "》已审核通过并发布");
                        notification.put("type", 1);
                        notification.put("relatedId", id);
                        
                        try {
                            interactionClient.sendNotification(notification);
                        } catch (Exception e) {
                            log.error("发送审核通过通知失败: userId={}, articleId={}", article.getUserId(), id, e);
                        }
                    }
                    
                    return true;
                } else {
                    log.error("文章审核通过但同步到搜索引擎失败: id={}", id);
                    return false;
                }
            } else {
                log.error("文章审核通过失败: id={}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("审核通过文章时发生异常: id={}", id, e);
            return false;
        }
    }

    @Override
    public boolean rejectArticle(Long id, String reviewComment) {
        try {
            Article article = getById(id);
            if (article == null) {
                log.warn("文章不存在，无法拒绝: id={}", id);
                return false;
            }

            if (article.getStatus() != 1) {
                log.warn("只有待审核状态的文章才能拒绝，当前状态: id={}, status={}", id, article.getStatus());
                return false;
            }

            article.setStatus(3);
            article.setUpdateTime(LocalDateTime.now());
            boolean result = updateById(article);

            if (result) {
                log.info("文章审核拒绝成功: id={}, title={}, 审核意见={}", id, article.getTitle(), reviewComment);
                
                if (article.getUserId() != null) {
                    Map<String, Object> notification = new HashMap<>();
                    notification.put("userId", article.getUserId());
                    notification.put("title", "您的文章审核未通过");
                    notification.put("content", "您的文章《" + article.getTitle() + "》审核未通过。" + 
                            (reviewComment != null && !reviewComment.isEmpty() ? "拒绝原因：" + reviewComment : "请修改后重新提交"));
                    notification.put("type", 1);
                    notification.put("relatedId", id);
                    
                    try {
                        interactionClient.sendNotification(notification);
                    } catch (Exception e) {
                        log.error("发送审核拒绝通知失败: userId={}, articleId={}", article.getUserId(), id, e);
                    }
                }
            } else {
                log.error("文章审核拒绝失败: id={}", id);
            }

            return result;
        } catch (Exception e) {
            log.error("拒绝文章时发生异常: id={}", id, e);
            return false;
        }
    }

    @Override
    public Page<Article> getPendingReviewArticles(Integer page, Integer size) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, 1)
                .orderByAsc(Article::getCreateTime);

        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public int getTotalViewCount() {
        List<Article> articles = list();
        return articles.stream()
                .mapToInt(article -> article.getViewCount() != null ? article.getViewCount() : 0)
                .sum();
    }

    @Override
    public boolean deleteArticle(Long id) {
        Article article = getById(id);
        if (article == null) {
            return false;
        }
        
        boolean result = removeById(id);
        
        if (result && article.getUserId() != null) {
            try {
                Boolean deleteResult = searchClient.deleteArticleIndex(id);
                if (deleteResult == null || !deleteResult) {
                    log.warn("删除文章索引失败: articleId={}", id);
                }
            } catch (Exception e) {
                log.error("删除文章索引时发生异常: articleId={}", id, e);
            }
            
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", article.getUserId());
            notification.put("title", "您的文章已被删除");
            notification.put("content", "您的文章《" + article.getTitle() + "》已被管理员删除，如有疑问请联系管理员");
            notification.put("type", 1);
            notification.put("relatedId", id);
            
            try {
                interactionClient.sendNotification(notification);
            } catch (Exception e) {
                log.error("发送文章删除通知失败: userId={}, articleId={}", article.getUserId(), id, e);
            }
        }
        
        return result;
    }
}

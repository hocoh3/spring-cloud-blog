package com.blog.search.service.impl;

import com.blog.search.entity.SearchArticle;
import com.blog.search.feign.ContentClient;
import com.blog.search.repository.SearchArticleRepository;
import com.blog.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchArticleRepository searchArticleRepository;
    private final ContentClient contentClient;

    @Override
    public Page<SearchArticle> searchArticles(String keyword, Pageable pageable) {
        return searchArticleRepository.searchArticles(keyword, pageable);
    }

    @Override
    public Page<SearchArticle> searchArticlesByCategory(String keyword, Long categoryId, Pageable pageable) {
        return searchArticleRepository.searchArticlesByCategory(keyword, categoryId, pageable);
    }

    @Override
    public boolean indexArticle(SearchArticle article) {
        searchArticleRepository.save(article);
        return true;
    }

    @Override
    public boolean updateArticleIndex(SearchArticle article) {
        searchArticleRepository.save(article);
        return true;
    }

    @Override
    public boolean deleteArticleIndex(Long articleId) {
        searchArticleRepository.deleteById(articleId);
        return true;
    }

    @Override
    public String syncAllArticles() {
        try {
            log.info("开始批量同步文章到搜索引擎");
            
            int page = 0;
            int size = 100;
            int totalSynced = 0;
            int totalFailed = 0;
            List<String> failedArticles = new ArrayList<>();
            
            while (true) {
                try {
                    Map<String, Object> result = contentClient.getArticleList(page, size, null, 2);
                    
                    if (result == null || !result.containsKey("records")) {
                        log.warn("获取文章列表失败，返回结果为空或缺少records字段");
                        break;
                    }
                    
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> records = (List<Map<String, Object>>) result.get("records");
                    
                    if (records == null || records.isEmpty()) {
                        log.info("第 {} 页没有更多文章数据，同步完成", page);
                        break;
                    }
                    
                    for (Map<String, Object> record : records) {
                        try {
                            SearchArticle searchArticle = new SearchArticle();
                            searchArticle.setId(((Number) record.get("id")).longValue());
                            searchArticle.setUserId(record.get("userId") != null ? ((Number) record.get("userId")).longValue() : null);
                            searchArticle.setTitle((String) record.get("title"));
                            searchArticle.setSummary((String) record.get("summary"));
                            searchArticle.setContent((String) record.get("content"));
                            searchArticle.setCategoryId(record.get("categoryId") != null ? ((Number) record.get("categoryId")).longValue() : null);
                            searchArticle.setCoverImage((String) record.get("coverImage"));
                            searchArticle.setViewCount(record.get("viewCount") != null ? ((Number) record.get("viewCount")).intValue() : 0);
                            searchArticle.setCommentCount(record.get("commentCount") != null ? ((Number) record.get("commentCount")).intValue() : 0);
                            searchArticle.setLikeCount(record.get("likeCount") != null ? ((Number) record.get("likeCount")).intValue() : 0);
                            searchArticle.setStatus(record.get("status") != null ? ((Number) record.get("status")).intValue() : 2);
                            searchArticle.setIsFeatured(record.get("isFeatured") != null ? ((Number) record.get("isFeatured")).intValue() : 0);
                            searchArticle.setIsTop(record.get("isTop") != null ? ((Number) record.get("isTop")).intValue() : 0);
                            
                            Object createTime = record.get("createTime");
                            if (createTime != null) {
                                searchArticle.setCreateTime(createTime.toString());
                            }
                            
                            Object updateTime = record.get("updateTime");
                            if (updateTime != null) {
                                searchArticle.setUpdateTime(updateTime.toString());
                            }
                            
                            searchArticleRepository.save(searchArticle);
                            totalSynced++;
                            
                            if (totalSynced % 10 == 0) {
                                log.info("已同步 {} 篇文章", totalSynced);
                            }
                        } catch (Exception e) {
                            log.error("同步文章失败: id={}, error={}", record.get("id"), e.getMessage());
                            totalFailed++;
                            failedArticles.add(String.valueOf(record.get("id")));
                        }
                    }
                    
                    page++;
                } catch (Exception e) {
                    log.error("获取第 {} 页文章列表失败: {}", page, e.getMessage());
                    break;
                }
            }
            
            String message = String.format("批量同步完成：成功 %d 篇，失败 %d 篇", totalSynced, totalFailed);
            if (!failedArticles.isEmpty()) {
                message += "，失败的文章ID: " + String.join(", ", failedArticles);
            }
            log.info(message);
            return message;
            
        } catch (Exception e) {
            log.error("批量同步文章到搜索引擎失败", e);
            return "批量同步失败: " + e.getMessage();
        }
    }
}
package com.blog.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.admin.entity.DataStatistics;
import com.blog.admin.feign.CommentClient;
import com.blog.admin.feign.ContentClient;
import com.blog.admin.feign.UserClient;
import com.blog.admin.mapper.DataStatisticsMapper;
import com.blog.admin.service.DataStatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataStatisticsServiceImpl extends ServiceImpl<DataStatisticsMapper, DataStatistics> implements DataStatisticsService {

    @Autowired
    private DataStatisticsMapper dataStatisticsMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ContentClient contentClient;

    @Autowired
    private CommentClient commentClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<DataStatistics> getDataByDateRange(Date startDate, Date endDate) {
        return dataStatisticsMapper.selectByDateRange(startDate, endDate);
    }

    @Override
    public DataStatistics getLatestData() {
        return dataStatisticsMapper.selectLatest();
    }

    @Override
    public Map<String, Object> getOverview() {
        Map<String, Object> overview = new HashMap<>();

        try {
            int userCount = getUserCount();
            int articleCount = getArticleCount();
            int commentCount = getCommentCount();
            int viewCount = getViewCount();

            overview.put("userCount", userCount);
            overview.put("articleCount", articleCount);
            overview.put("commentCount", commentCount);
            overview.put("viewCount", viewCount);

            DataStatistics previousDay = dataStatisticsMapper.selectPreviousDay();
            if (previousDay != null) {
                int userTrend = previousDay.getUserCount() != null && previousDay.getUserCount() > 0 
                    ? (int) Math.round((userCount - previousDay.getUserCount()) * 100.0 / previousDay.getUserCount()) 
                    : 0;
                int articleTrend = previousDay.getArticleCount() != null && previousDay.getArticleCount() > 0 
                    ? (int) Math.round((articleCount - previousDay.getArticleCount()) * 100.0 / previousDay.getArticleCount()) 
                    : 0;
                int commentTrend = previousDay.getCommentCount() != null && previousDay.getCommentCount() > 0 
                    ? (int) Math.round((commentCount - previousDay.getCommentCount()) * 100.0 / previousDay.getCommentCount()) 
                    : 0;
                int viewTrend = previousDay.getViewCount() != null && previousDay.getViewCount() > 0 
                    ? (int) Math.round((viewCount - previousDay.getViewCount()) * 100.0 / previousDay.getViewCount()) 
                    : 0;

                overview.put("userTrend", userTrend);
                overview.put("articleTrend", articleTrend);
                overview.put("commentTrend", commentTrend);
                overview.put("viewTrend", viewTrend);
            } else {
                DataStatistics today = dataStatisticsMapper.selectToday();
                if (today != null) {
                    int userTrend = today.getUserCount() != null && today.getUserCount() > 0 
                        ? (int) Math.round((userCount - today.getUserCount()) * 100.0 / today.getUserCount()) 
                        : 0;
                    int articleTrend = today.getArticleCount() != null && today.getArticleCount() > 0 
                        ? (int) Math.round((articleCount - today.getArticleCount()) * 100.0 / today.getArticleCount()) 
                        : 0;
                    int commentTrend = today.getCommentCount() != null && today.getCommentCount() > 0 
                        ? (int) Math.round((commentCount - today.getCommentCount()) * 100.0 / today.getCommentCount()) 
                        : 0;
                    int viewTrend = today.getViewCount() != null && today.getViewCount() > 0 
                        ? (int) Math.round((viewCount - today.getViewCount()) * 100.0 / today.getViewCount()) 
                        : 0;

                    overview.put("userTrend", userTrend);
                    overview.put("articleTrend", articleTrend);
                    overview.put("commentTrend", commentTrend);
                    overview.put("viewTrend", viewTrend);
                } else {
                    overview.put("userTrend", 0);
                    overview.put("articleTrend", 0);
                    overview.put("commentTrend", 0);
                    overview.put("viewTrend", 0);
                }
            }
        } catch (Exception e) {
            System.err.println("获取统计数据失败: " + e.getMessage());
            overview.put("userCount", 0);
            overview.put("articleCount", 0);
            overview.put("commentCount", 0);
            overview.put("viewCount", 0);
            overview.put("userTrend", 0);
            overview.put("articleTrend", 0);
            overview.put("commentTrend", 0);
            overview.put("viewTrend", 0);
        }

        overview.put("trend", new ArrayList<>());

        return overview;
    }

    private int getUserCount() {
        try {
            String response = userClient.getUserList(1, 1, null);
            if (response != null) {
                Map<String, Object> body = objectMapper.readValue(response, Map.class);
                if (body.containsKey("total")) {
                    return ((Number) body.get("total")).intValue();
                } else if (body.containsKey("records")) {
                    List<?> records = (List<?>) body.get("records");
                    return records != null ? records.size() : 0;
                }
            }
        } catch (Exception e) {
            System.err.println("获取用户数量失败: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private int getArticleCount() {
        try {
            String response = contentClient.getArticleCount();
            if (response != null) {
                Map<String, Object> body = objectMapper.readValue(response, Map.class);
                if (body.containsKey("total")) {
                    return ((Number) body.get("total")).intValue();
                } else if (body.containsKey("records")) {
                    List<?> records = (List<?>) body.get("records");
                    return records != null ? records.size() : 0;
                }
            }
        } catch (Exception e) {
            System.err.println("获取文章数量失败: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private int getCommentCount() {
        try {
            String response = commentClient.getCommentCount();
            if (response != null) {
                Map<String, Object> body = objectMapper.readValue(response, Map.class);
                if (body.containsKey("total")) {
                    return ((Number) body.get("total")).intValue();
                } else if (body.containsKey("records")) {
                    List<?> records = (List<?>) body.get("records");
                    return records != null ? records.size() : 0;
                } else if (body.containsKey("pages")) {
                    return ((Number) body.get("pages")).intValue() * 10;
                }
            }
        } catch (Exception e) {
            System.err.println("获取评论数量失败: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    private int getViewCount() {
        try {
            String response = contentClient.getArticleStatistics();
            if (response != null) {
                Map<String, Object> body = objectMapper.readValue(response, Map.class);
                if (body.containsKey("totalViews")) {
                    return ((Number) body.get("totalViews")).intValue();
                }
            }
        } catch (Exception e) {
            System.err.println("获取浏览量失败: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void collectDailyStatistics() {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date today = calendar.getTime();

            DataStatistics existing = dataStatisticsMapper.selectByDate(today);
            if (existing != null) {
                return;
            }

            int userCount = getUserCount();
            int articleCount = getArticleCount();
            int commentCount = getCommentCount();
            int viewCount = getViewCount();

            DataStatistics statistics = new DataStatistics();
            statistics.setStatDate(today);
            statistics.setUserCount(userCount);
            statistics.setArticleCount(articleCount);
            statistics.setCommentCount(commentCount);
            statistics.setViewCount(viewCount);

            dataStatisticsMapper.insert(statistics);
        } catch (Exception e) {
            System.err.println("收集每日统计数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.blog.admin.controller;

import com.blog.admin.entity.DataStatistics;
import com.blog.admin.service.DataStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/statistics")
public class DataStatisticsController {

    @Autowired
    private DataStatisticsService dataStatisticsService;

    /**
     * 获取数据概览
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> overview = dataStatisticsService.getOverview();
        return ResponseEntity.ok(overview);
    }

    /**
     * 获取数据趋势
     */
    @GetMapping("/trend")
    public ResponseEntity<List<DataStatistics>> getTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<DataStatistics> trendData = dataStatisticsService.getDataByDateRange(startDate, endDate);
        return ResponseEntity.ok(trendData);
    }

    /**
     * 获取最新统计数据
     */
    @GetMapping("/latest")
    public ResponseEntity<DataStatistics> getLatestData() {
        DataStatistics latest = dataStatisticsService.getLatestData();
        return ResponseEntity.ok(latest);
    }
}
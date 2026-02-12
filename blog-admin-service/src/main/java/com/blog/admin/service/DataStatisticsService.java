package com.blog.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.admin.entity.DataStatistics;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DataStatisticsService extends IService<DataStatistics> {

    /**
     * 获取指定日期范围的数据统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据列表
     */
    List<DataStatistics> getDataByDateRange(Date startDate, Date endDate);

    /**
     * 获取最新的统计数据
     * @return 最新统计数据
     */
    DataStatistics getLatestData();

    /**
     * 获取数据概览
     * @return 概览数据
     */
    Map<String, Object> getOverview();

    /**
     * 收集每日统计数据
     */
    void collectDailyStatistics();
}
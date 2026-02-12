package com.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.admin.entity.DataStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DataStatisticsMapper extends BaseMapper<DataStatistics> {

    /**
     * 获取指定日期范围的数据统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据列表
     */
    List<DataStatistics> selectByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 获取最新的统计数据
     * @return 最新统计数据
     */
    DataStatistics selectLatest();

    /**
     * 根据日期获取统计数据
     * @param date 日期
     * @return 统计数据
     */
    DataStatistics selectByDate(@Param("date") Date date);

    /**
     * 获取今天的统计数据
     * @return 今天的统计数据
     */
    DataStatistics selectToday();

    /**
     * 获取今天之前的最新统计数据（不包括今天）
     * @return 前一天的统计数据
     */
    DataStatistics selectPreviousDay();
}
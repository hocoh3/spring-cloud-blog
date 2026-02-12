package com.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.blog.admin.entity.ContentReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContentReviewMapper extends BaseMapper<ContentReview> {

    /**
     * 分页查询内容审核列表
     * @param page 分页对象
     * @param reviewStatus 审核状态
     * @param keyword 关键词(标题、作者)
     * @return 分页结果
     */
    Page<ContentReview> selectContentReviewPage(
            Page<ContentReview> page,
            @Param("reviewStatus") Integer reviewStatus,
            @Param("keyword") String keyword
    );
}
package com.blog.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.admin.entity.ContentReview;

public interface ContentReviewService extends IService<ContentReview> {

    /**
     * 分页查询内容审核列表
     * @param page 页码
     * @param size 每页大小
     * @param reviewStatus 审核状态
     * @param keyword 关键词
     * @return 分页结果
     */
    Page<ContentReview> getContentReviewList(int page, int size, Integer reviewStatus, String keyword);

    /**
     * 通过审核
     * @param id 审核ID
     * @param adminId 审核管理员ID
     * @param adminName 审核管理员名称
     * @param comment 审核意见
     * @return 是否成功
     */
    boolean approveReview(Long id, Long adminId, String adminName, String comment);

    /**
     * 拒绝审核
     * @param id 审核ID
     * @param adminId 审核管理员ID
     * @param adminName 审核管理员名称
     * @param comment 审核意见
     * @return 是否成功
     */
    boolean rejectReview(Long id, Long adminId, String adminName, String comment);
}
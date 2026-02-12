package com.blog.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.admin.entity.ContentReview;
import com.blog.admin.mapper.ContentReviewMapper;
import com.blog.admin.service.ContentReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class ContentReviewServiceImpl extends ServiceImpl<ContentReviewMapper, ContentReview> implements ContentReviewService {

    @Autowired
    private ContentReviewMapper contentReviewMapper;

    @Override
    public Page<ContentReview> getContentReviewList(int page, int size, Integer reviewStatus, String keyword) {
        Page<ContentReview> pageInfo = new Page<>(page, size);
        return contentReviewMapper.selectContentReviewPage(pageInfo, reviewStatus, keyword);
    }

    @Transactional
    @Override
    public boolean approveReview(Long id, Long adminId, String adminName, String comment) {
        ContentReview contentReview = getById(id);
        if (contentReview == null) {
            return false;
        }

        contentReview.setReviewStatus(1); // 审核通过
        contentReview.setReviewAdminId(adminId);
        contentReview.setReviewAdminName(adminName);
        contentReview.setReviewComment(comment);
        contentReview.setReviewTime(new Date());
        contentReview.setUpdateTime(new Date());

        return updateById(contentReview);
    }

    @Transactional
    @Override
    public boolean rejectReview(Long id, Long adminId, String adminName, String comment) {
        ContentReview contentReview = getById(id);
        if (contentReview == null) {
            return false;
        }

        contentReview.setReviewStatus(2); // 审核不通过
        contentReview.setReviewAdminId(adminId);
        contentReview.setReviewAdminName(adminName);
        contentReview.setReviewComment(comment);
        contentReview.setReviewTime(new Date());
        contentReview.setUpdateTime(new Date());

        return updateById(contentReview);
    }
}
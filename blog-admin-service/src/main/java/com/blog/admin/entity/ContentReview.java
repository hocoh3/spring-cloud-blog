package com.blog.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("content_review")
public class ContentReview implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private String title;

    private String content;

    private Long authorId;

    private String authorName;

    /**
     * 审核状态(0:待审核,1:审核通过,2:审核不通过)
     */
    private Integer reviewStatus;

    private Long reviewAdminId;

    private String reviewAdminName;

    private Date reviewTime;

    private String reviewComment;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
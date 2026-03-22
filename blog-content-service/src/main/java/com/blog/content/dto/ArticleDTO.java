package com.blog.content.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ArticleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Long authorId;

    private String authorName;

    private String title;

    private String summary;

    private String content;

    private String coverImage;

    private Long categoryId;

    private String categoryName;

    private Integer viewCount;

    private Integer commentCount;

    private Integer likeCount;

    private Integer status;

    private Integer isFeatured;

    private Integer isTop;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

package com.blog.interaction.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CommentVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long articleId;

    private Long userId;

    private Long parentId;

    private String content;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String authorName;

    private String avatar;
}

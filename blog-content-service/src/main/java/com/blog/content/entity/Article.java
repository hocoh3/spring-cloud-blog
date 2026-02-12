package com.blog.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String title;

    private String summary;

    private String content;

    private String coverImage;

    private Long categoryId;

    private Integer viewCount;

    private Integer commentCount;

    private Integer likeCount;

    private Integer status;

    private Integer isFeatured;

    private Integer isTop;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}

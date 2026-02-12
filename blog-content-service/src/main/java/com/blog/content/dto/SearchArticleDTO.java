package com.blog.content.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchArticleDTO {
    private Long id;
    private Long userId;
    private String title;
    private String summary;
    private String content;
    private Long categoryId;
    private String coverImage;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private Integer status;
    private Integer isFeatured;
    private Integer isTop;
    private String createTime;
    private String updateTime;
}

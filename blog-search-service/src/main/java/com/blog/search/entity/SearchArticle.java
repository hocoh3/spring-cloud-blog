package com.blog.search.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(indexName = "blog_article", shards = 3, replicas = 1)
public class SearchArticle {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword, index = false)
    private Long userId;

    @Field(type = FieldType.Keyword, index = false)
    private Long authorId;

    @Field(type = FieldType.Keyword, index = false)
    private String authorName;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String summary;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Keyword)
    private Long categoryId;

    @Field(type = FieldType.Keyword, index = false)
    private String categoryName;

    @Field(type = FieldType.Keyword, index = false)
    private String coverImage;

    @Field(type = FieldType.Integer, index = false)
    private Integer viewCount;

    @Field(type = FieldType.Integer, index = false)
    private Integer commentCount;

    @Field(type = FieldType.Integer, index = false)
    private Integer likeCount;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Integer, index = false)
    private Integer isFeatured;

    @Field(type = FieldType.Integer, index = false)
    private Integer isTop;

    @Field(type = FieldType.Keyword)
    private String createTime;

    @Field(type = FieldType.Keyword)
    private String updateTime;
}
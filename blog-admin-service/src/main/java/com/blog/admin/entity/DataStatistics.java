package com.blog.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("data_statistics")
public class DataStatistics implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Date statDate;

    private Integer userCount;

    private Integer articleCount;

    private Integer commentCount;

    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
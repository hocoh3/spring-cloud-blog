package com.blog.interaction.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.interaction.entity.Comment;
import com.blog.interaction.vo.CommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface CommentService extends IService<Comment> {

    Page<CommentVO> getCommentsByArticleId(Long articleId, Integer page, Integer size);

    Page<CommentVO> getCommentsByUserId(Long userId, Integer page, Integer size);

    boolean addComment(Comment comment);

    boolean updateCommentStatus(Long id, Integer status);

    boolean deleteComment(Long id);
}

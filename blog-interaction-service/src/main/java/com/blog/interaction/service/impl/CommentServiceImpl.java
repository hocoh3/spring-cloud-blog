package com.blog.interaction.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.interaction.entity.Comment;
import com.blog.interaction.feign.ContentClient;
import com.blog.interaction.feign.DataClient;
import com.blog.interaction.feign.UserClient;
import com.blog.interaction.mapper.CommentMapper;
import com.blog.interaction.service.CommentService;
import com.blog.interaction.service.InternalNotificationService;
import com.blog.interaction.vo.CommentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserClient userClient;

    @Autowired
    private InternalNotificationService internalNotificationService;

    @Autowired
    private ContentClient contentClient;

    @Autowired
    private DataClient dataClient;

    @Override
    public Page<CommentVO> getCommentsByArticleId(Long articleId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId, articleId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreateTime);

        Page<Comment> commentPage = page(new Page<>(page, size), queryWrapper);
        
        List<Long> userIds = commentPage.getRecords().stream()
                .map(Comment::getUserId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, UserClient.UserDTO> userMap = getUsersByIds(userIds);
        
        List<CommentVO> commentVOList = new ArrayList<>();
        for (Comment comment : commentPage.getRecords()) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            
            UserClient.UserDTO user = userMap.get(comment.getUserId());
            if (user != null) {
                commentVO.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                commentVO.setAvatar(user.getAvatar());
            }
            
            commentVOList.add(commentVO);
        }
        
        Page<CommentVO> commentVOPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        commentVOPage.setRecords(commentVOList);
        commentVOPage.setPages(commentPage.getPages());
        
        return commentVOPage;
    }

    private void sendReplyNotification(Comment comment) {
        if (comment.getParentId() == null) {
            return;
        }

        try {
            Comment parentComment = getById(comment.getParentId());
            if (parentComment != null && !parentComment.getUserId().equals(comment.getUserId())) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("userId", parentComment.getUserId());
                notification.put("title", "有人回复了你的评论");
                notification.put("content", comment.getContent());
                notification.put("type", 1);
                notification.put("relatedId", comment.getArticleId());
                
                try {
                    internalNotificationService.sendNotification(notification);
                } catch (Exception e) {
                    System.err.println("发送回复通知失败: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("获取父评论失败: " + e.getMessage());
        }
    }

    @Override
    public Page<CommentVO> getCommentsByUserId(Long userId, Integer page, Integer size) {
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getUserId, userId)
                .orderByDesc(Comment::getCreateTime);

        Page<Comment> commentPage = page(new Page<>(page, size), queryWrapper);
        
        ResponseEntity<UserClient.UserDTO> response = userClient.getUserById(userId);
        UserClient.UserDTO user = (response != null && response.getStatusCode().is2xxSuccessful()) ? response.getBody() : null;
        
        List<CommentVO> commentVOList = new ArrayList<>();
        for (Comment comment : commentPage.getRecords()) {
            CommentVO commentVO = new CommentVO();
            BeanUtils.copyProperties(comment, commentVO);
            
            if (user != null) {
                commentVO.setAuthorName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                commentVO.setAvatar(user.getAvatar());
            }
            
            commentVOList.add(commentVO);
        }
        
        Page<CommentVO> commentVOPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        commentVOPage.setRecords(commentVOList);
        commentVOPage.setPages(commentPage.getPages());
        
        return commentVOPage;
    }

    private Map<Long, UserClient.UserDTO> getUsersByIds(List<Long> userIds) {
        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        userId -> {
                            try {
                                ResponseEntity<UserClient.UserDTO> response = userClient.getUserById(userId);
                                return (response != null && response.getStatusCode().is2xxSuccessful()) ? response.getBody() : null;
                            } catch (Exception e) {
                                return null;
                            }
                        },
                        (existing, replacement) -> existing
                ));
    }

    @Override
    public boolean addComment(Comment comment) {
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        comment.setStatus(1);
        
        boolean result = save(comment);
        
        if (result) {
            sendMentionNotifications(comment);
            sendReplyNotification(comment);
            
            try {
                LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Comment::getArticleId, comment.getArticleId())
                        .eq(Comment::getStatus, 1);
                long commentCount = count(queryWrapper);
                contentClient.updateCommentCount(comment.getArticleId(), (int) commentCount);
            } catch (Exception e) {
                System.err.println("更新文章评论数失败: " + e.getMessage());
            }
            
            if (comment.getUserId() != null) {
                try {
                    dataClient.updateUserActivity(comment.getUserId());
                } catch (Exception e) {
                    System.err.println("更新用户活跃度失败: " + e.getMessage());
                }
            }
        }
        
        return result;
    }

    private void sendMentionNotifications(Comment comment) {
        String content = comment.getContent();
        if (content == null || content.isEmpty()) {
            return;
        }

        Pattern pattern = Pattern.compile("@([\\u4e00-\\u9fa5a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(content);
        
        Set<String> mentionedUsernames = new HashSet<>();
        while (matcher.find()) {
            mentionedUsernames.add(matcher.group(1));
        }

        Map<String, UserClient.UserDTO> allUsers = getAllUsers();
        
        for (String username : mentionedUsernames) {
            UserClient.UserDTO mentionedUser = allUsers.get(username);
            if (mentionedUser != null && !mentionedUser.getId().equals(comment.getUserId())) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("userId", mentionedUser.getId());
                notification.put("title", "有人在评论中提到了你");
                notification.put("content", comment.getContent());
                notification.put("type", 1);
                notification.put("relatedId", comment.getArticleId());
                
                try {
                    internalNotificationService.sendNotification(notification);
                } catch (Exception e) {
                    System.err.println("发送@用户通知失败: " + e.getMessage());
                }
            }
        }
    }

    private Map<String, UserClient.UserDTO> getAllUsers() {
        try {
            ResponseEntity<List<UserClient.UserDTO>> response = userClient.getAllUsers();
            if (response != null && response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().stream()
                        .collect(Collectors.toMap(
                                user -> user.getUsername(),
                                user -> user,
                                (existing, replacement) -> existing
                        ));
            }
        } catch (Exception e) {
            System.err.println("获取用户列表失败: " + e.getMessage());
        }
        return new HashMap<>();
    }

    @Override
    public boolean updateCommentStatus(Long id, Integer status) {
        return lambdaUpdate()
                .set(Comment::getStatus, status)
                .set(Comment::getUpdateTime, LocalDateTime.now())
                .eq(Comment::getId, id)
                .update();
    }

    @Override
    public boolean deleteComment(Long id) {
        Comment comment = getById(id);
        if (comment == null) {
            return false;
        }
        
        boolean result = removeById(id);
        
        if (result) {
            if (comment.getUserId() != null) {
                String articleTitle = "某篇文章";
                try {
                    ContentClient.ArticleDTO article = contentClient.getArticleById(comment.getArticleId());
                    if (article != null && article.getTitle() != null) {
                        articleTitle = article.getTitle();
                    }
                } catch (Exception e) {
                    System.err.println("获取文章标题失败: " + e.getMessage());
                }
                
                Map<String, Object> notification = new HashMap<>();
                notification.put("userId", comment.getUserId());
                notification.put("title", "您的评论已被删除");
                notification.put("content", "您在《" + articleTitle + "》下的评论已被管理员删除，如有疑问请联系管理员");
                notification.put("type", 1);
                notification.put("relatedId", comment.getArticleId());
                
                try {
                    internalNotificationService.sendNotification(notification);
                } catch (Exception e) {
                    System.err.println("发送评论删除通知失败: " + e.getMessage());
                }
            }
            
            try {
                LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(Comment::getArticleId, comment.getArticleId())
                        .eq(Comment::getStatus, 1);
                long commentCount = count(queryWrapper);
                contentClient.updateCommentCount(comment.getArticleId(), (int) commentCount);
            } catch (Exception e) {
                System.err.println("更新文章评论数失败: " + e.getMessage());
            }
        }
        
        return result;
    }
}

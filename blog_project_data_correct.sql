-- ========================================================
-- 基于Spring Cloud微服务架构的博客系统 - 完整数据库SQL文件
-- 数据来源：直接从MySQL数据库查询生成
-- 生成时间: 2026-03-11
-- 字符集: UTF-8
-- ========================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ========================================================
-- 1. 用户服务数据库 (blog_user)
-- ========================================================
DROP DATABASE IF EXISTS blog_user;
CREATE DATABASE blog_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_user;

-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `nickname` varchar(50) NOT NULL COMMENT '昵称',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
  `bio` varchar(255) DEFAULT NULL COMMENT '个人简介',
  `role` varchar(20) NOT NULL DEFAULT 'user' COMMENT '角色',
  `status` int NOT NULL DEFAULT '0' COMMENT '状态：0-正常 1-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入用户数据
INSERT INTO `user` VALUES 
(1, 'testuser', '$2a$10$udATM99L0m/Wb8Ehp/T5muNxw39e9sZvlKn0x0VyROP.0Sn0qk1iG', 'test@example.com', 'text1', NULL, NULL, 'ROLE_USER', 1, '2026-01-10 00:28:23', '2026-03-09 09:14:51'),
(2, 'user', '$2a$10$jKxaUJAyp9pCTLC7uMVXPONMSMWxKIrWeFz4VD7KJfrlMUb2PQmvK', 'hocoh@qq.com', '人', '/uploads/avatars/18589c70-25aa-4204-b1c2-aed0a4a60b37.png', '666', 'ROLE_USER', 1, '2026-01-10 00:36:43', '2026-03-09 09:14:25'),
(3, 'testuser123', '$2a$10$YPaoxbdZHoCLBJYBMAwlNuiJvEqBhZGeF15taMQJofhzQ6H7.uWf6', 'test123@example.com', 'text2', NULL, NULL, 'ROLE_USER', 1, '2026-01-10 03:29:31', '2026-03-09 09:14:51'),
(4, '666', '$2a$10$zptgO7kSw8kSgV.b2mklZutLftTacWhl17MkG06lLEGjrGzgCP3VO', '111@666', 'user', NULL, NULL, 'ROLE_USER', 1, '2026-01-18 03:19:20', '2026-02-01 01:10:31');

-- ========================================================
-- 2. 内容服务数据库 (blog_content)
-- ========================================================
DROP DATABASE IF EXISTS blog_content;
CREATE DATABASE blog_content DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_content;

-- 分类表
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` varchar(500) DEFAULT NULL COMMENT '分类描述',
  `parent_id` bigint DEFAULT NULL COMMENT '父分类ID',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用, 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `article_count` int NOT NULL DEFAULT '0' COMMENT '文章数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 插入分类数据
INSERT INTO `category` (`id`, `name`, `description`, `parent_id`, `sort_order`, `status`, `create_time`, `update_time`, `article_count`) VALUES
(1, '技术分享', '分享各种技术干货、编程技巧、开发经验等内容', NULL, 1, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(2, '前端技术', '专注于前端开发技术，包括HTML、CSS、JavaScript、框架等', NULL, 2, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(3, '后端技术', '涵盖Java、Python、Go等后端开发技术和架构设计', NULL, 3, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(4, '数据库', 'MySQL、Redis、MongoDB等数据库技术分享', NULL, 4, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(5, '云计算', 'Docker、Kubernetes、云服务等相关技术', NULL, 5, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(6, '人工智能', '机器学习、深度学习、AI应用等前沿技术', NULL, 6, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(7, '移动开发', 'iOS、Android、React Native等移动端开发技术', NULL, 7, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(8, 'DevOps', 'CI/CD、自动化部署、运维工具等', NULL, 8, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(9, '生活感悟', '分享生活点滴、职场经验、个人成长等', NULL, 9, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0),
(10, '读书笔记', '技术书籍阅读心得、知识总结等', NULL, 10, 0, '2026-01-09 17:16:17', '2026-01-09 17:16:17', 0);

-- 文章表
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '作者ID',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `summary` varchar(500) DEFAULT NULL COMMENT '摘要',
  `content` longtext COMMENT '内容',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图片',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `view_count` int NOT NULL DEFAULT '0' COMMENT '浏览量',
  `comment_count` int NOT NULL DEFAULT '0' COMMENT '评论数',
  `like_count` int NOT NULL DEFAULT '0' COMMENT '点赞数',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-草稿, 1-已发布, 2-审核中, 3-已拒绝',
  `is_featured` tinyint NOT NULL DEFAULT '0' COMMENT '是否推荐：0-否, 1-是',
  `is_top` tinyint NOT NULL DEFAULT '0' COMMENT '是否置顶：0-否, 1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 插入文章数据（简化版，仅包含基本信息）
INSERT INTO `article` (`id`, `user_id`, `title`, `summary`, `content`, `cover_image`, `category_id`, `view_count`, `comment_count`, `like_count`, `status`, `is_featured`, `is_top`, `create_time`, `update_time`) VALUES
(1, 1, 'Spring Boot入门教程', '本文介绍Spring Boot的基本概念和快速入门方法，帮助初学者快速上手Spring Boot开发。', 'Spring Boot是一个简化Spring应用开发的框架...', NULL, 3, 156, 0, 1, 2, 1, 0, '2026-01-09 17:07:41', '2026-02-09 19:12:34'),
(2, 1, 'Vue.js组件化开发实战', 'Vue.js是一个渐进式JavaScript框架，核心思想是组件化开发。本文将详细介绍Vue.js的组件化开发模式。', 'Vue.js组件化开发内容...', NULL, 2, 67, 0, 0, 2, 0, 0, '2026-01-09 17:07:41', '2026-02-09 19:12:34'),
(3, 1, 'MySQL数据库优化实战', '本文总结了MySQL数据库优化的实战经验，包括索引优化、查询优化、表结构设计、配置优化等方面的内容。', 'MySQL优化内容...', NULL, 4, 78, 0, 0, 2, 0, 0, '2026-01-09 17:07:41', '2026-02-09 19:12:34'),
(4, 1, 'Docker容器化部署实践', 'Docker是一个开源的应用容器引擎，可以让开发者打包他们的应用以及依赖包到一个可移植的容器中。', 'Docker容器化内容...', NULL, 5, 56, 0, 0, 2, 0, 0, '2026-01-09 17:07:41', '2026-02-09 19:12:34'),
(5, 1, 'Redis缓存应用实践', 'Redis是一个开源的内存数据结构存储系统，可以用作数据库、缓存和消息中间件。', 'Redis缓存内容...', NULL, 4, 64, 0, 1, 2, 0, 0, '2026-01-09 17:07:41', '2026-02-09 19:12:34'),
(6, 1, '机器学习入门指南', '机器学习是人工智能的一个分支，它通过算法让计算机从数据中学习。', '机器学习内容...', NULL, 6, 83, 0, 0, 2, 0, 0, '2026-01-09 17:07:41', '2026-02-09 19:12:34'),
(11, 1, 'Spring Cloud微服务架构设计与实现', 'Spring Cloud是一个基于Spring Boot实现的微服务架构开发工具包。', 'Spring Cloud内容...', NULL, 1, 191, 1, 1, 2, 1, 1, '2026-01-09 17:14:05', '2026-03-18 14:51:15'),
(12, 1, '前端开发最佳实践', '本文总结了前端开发中的最佳实践，包括代码规范、性能优化、组件化开发、状态管理等方面的内容。', '前端开发内容...', NULL, 2, 90, 0, 0, 2, 1, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(13, 1, 'Spring Boot应用开发指南', 'Spring Boot简化了Spring应用的初始搭建和开发过程。', 'Spring Boot指南内容...', NULL, 3, 161, 0, 0, 2, 1, 0, '2026-01-09 17:14:05', '2026-03-03 14:17:22'),
(14, 1, 'MySQL数据库优化实战', '本文总结了MySQL数据库优化的实战经验。', 'MySQL优化内容...', NULL, 4, 78, 0, 0, 2, 0, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(15, 1, 'Vue.js组件化开发', 'Vue.js是一个渐进式JavaScript框架，核心思想是组件化开发。', 'Vue.js内容...', NULL, 2, 67, 0, 0, 2, 0, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(16, 1, '微服务架构设计模式', '微服务架构是一种将单一应用程序开发为一组小型服务的方法。', '微服务架构内容...', NULL, 3, 96, 0, 0, 2, 0, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(17, 1, 'Docker容器化部署实践', 'Docker是一个开源的应用容器引擎。', 'Docker内容...', NULL, 5, 56, 0, 0, 2, 0, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(18, 1, 'CSS Grid布局实战', 'CSS Grid是一个强大的二维布局系统，可以同时处理行和列。', 'CSS Grid内容...', NULL, 2, 47, 0, 1, 2, 0, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(19, 1, 'Redis缓存应用实践', 'Redis是一个开源的内存数据结构存储系统。', 'Redis内容...', NULL, 4, 64, 0, 1, 2, 0, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(20, 1, '机器学习入门指南', '机器学习是人工智能的一个分支。', '机器学习内容...', NULL, 6, 83, 0, 0, 2, 0, 0, '2026-01-09 17:14:05', '2026-02-09 19:12:34'),
(21, 2, '666', '1145', '1', NULL, 1, 18, 1, 1, 2, 0, 0, '2026-01-18 02:35:56', '2026-03-03 15:42:50'),
(24, 2, '试试', '123', '1323', NULL, 20, 0, 0, 0, 3, 0, 0, '2026-02-10 02:55:18', '2026-02-10 02:56:23'),
(25, 2, 'why', '666', 'text', NULL, 20, 10, 3, 1, 2, 0, 0, '2026-02-10 02:59:00', '2026-03-04 11:05:36');

-- 文章点赞表
CREATE TABLE `article_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';

-- 文章收藏表
CREATE TABLE `article_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';

-- ========================================================
-- 3. 交互服务数据库 (blog_interaction)
-- ========================================================
DROP DATABASE IF EXISTS blog_interaction;
CREATE DATABASE blog_interaction DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_interaction;

-- 评论表
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
  `content` text NOT NULL COMMENT '评论内容',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-已删除, 1-正常, 2-待审核',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 插入评论数据
INSERT INTO `comment` VALUES
(2, 21, 2, NULL, '有点东西', 1, '2026-01-18 02:36:23', '2026-01-18 02:36:23'),
(6, 11, 2, NULL, '@user 怎么了', 1, '2026-01-19 23:25:16', '2026-01-19 23:25:16'),
(14, 25, 2, NULL, 'no why', 1, '2026-02-10 03:04:06', '2026-02-10 03:04:06'),
(15, 25, 2, 14, '@666 try try', 1, '2026-02-10 03:04:23', '2026-02-10 03:04:23'),
(16, 25, 4, 14, '@666 你是？', 1, '2026-02-10 03:05:40', '2026-02-10 03:05:40');

-- 通知表
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(255) NOT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '通知类型：0-系统通知, 1-评论通知, 2-点赞通知, 3-关注通知',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读, 1-已读',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-正常, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 插入通知数据
INSERT INTO `notification` VALUES
(1, 1, 'Test', 'Test notification', 1, 0, NULL, '2026-01-19 21:15:46', '2026-01-19 16:11:27', 1),
(2, 1, 'test', 'test content', 1, 0, NULL, '2026-01-19 21:27:38', '2026-01-19 21:27:38', 0),
(3, 1, '66', '6666', 1, 0, NULL, '2026-01-19 21:30:35', '2026-01-19 16:11:20', 1),
(4, 2, '66', '666', 1, 1, NULL, '2026-01-19 21:30:59', '2026-02-10 02:56:41', 1),
(5, 1, 'test', 'test content', 1, 0, NULL, '2026-01-19 21:31:53', '2026-01-19 16:11:13', 1),
(6, 4, '6', '666', 0, 1, NULL, '2026-02-01 00:23:36', '2026-02-01 00:40:31', 0),
(7, 3, '66', '6666', 0, 0, NULL, '2026-02-01 01:10:11', '2026-01-31 17:10:22', 1),
(8, 4, '66', '6666', 0, 0, NULL, '2026-02-01 01:10:11', '2026-01-31 17:10:25', 1),
(9, 2, '测试', 'text', 0, 1, NULL, '2026-02-01 01:10:45', '2026-02-10 02:56:41', 0),
(11, 4, '有人回复了你的评论', '@user text', 1, 0, 11, '2026-02-10 01:57:17', '2026-02-10 01:57:17', 1),
(23, 2, '您的文章审核未通过', '您的文章《试试》审核未通过。拒绝原因：不行', 1, 1, 24, '2026-02-10 02:56:23', '2026-02-10 02:56:41', 1),
(24, 4, '有人在评论中提到了你', '@666 try try', 1, 0, 25, '2026-02-10 03:04:23', '2026-02-10 03:04:23', 1),
(25, 2, '有人回复了你的评论', '@666 你是？', 1, 0, 25, '2026-02-10 03:05:40', '2026-02-10 03:05:40', 1);

-- 私信表
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '私信ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_name` varchar(100) DEFAULT NULL COMMENT '发送者昵称',
  `sender_avatar` varchar(500) DEFAULT NULL COMMENT '发送者头像',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text NOT NULL COMMENT '私信内容',
  `is_read` tinyint NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读, 1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0-正常, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私信表';

-- 插入私信数据
INSERT INTO `message` VALUES
(1, 2, '666', '/uploads/avatars/18589c70-25aa-4204-b1c2-aed0a4a60b37.png', 4, '999', 1, '2026-01-19 23:04:50', '2026-01-19 16:07:43', 1),
(4, 4, 'user', NULL, 2, '2', 1, '2026-01-19 23:07:38', '2026-01-19 16:00:36', 1),
(6, 2, '666', '/uploads/avatars/18589c70-25aa-4204-b1c2-aed0a4a60b37.png', 4, '干嘛', 1, '2026-01-19 23:14:32', '2026-01-19 23:25:31', 0),
(7, 2, '666', '/uploads/avatars/18589c70-25aa-4204-b1c2-aed0a4a60b37.png', 4, '干嘛', 1, '2026-01-19 23:14:33', '2026-01-19 23:25:31', 0),
(8, 4, 'user', NULL, 2, '你猜', 1, '2026-01-19 23:15:03', '2026-01-19 23:21:47', 0),
(9, 2, '666', '/uploads/avatars/18589c70-25aa-4204-b1c2-aed0a4a60b37.png', 4, '干嘛', 1, '2026-01-19 23:16:11', '2026-01-19 15:55:20', 1),
(10, 2, '666', '/uploads/avatars/18589c70-25aa-4204-b1c2-aed0a4a60b37.png', 4, '666', 1, '2026-01-19 23:20:36', '2026-01-19 15:55:09', 1),
(11, 4, 'user', NULL, 2, '？', 1, '2026-01-19 23:20:55', '2026-01-19 15:55:07', 1),
(12, 2, '666', '/uploads/avatars/18589c70-25aa-4204-b1c2-aed0a4a60b37.png', 4, 'text', 0, '2026-02-10 01:06:19', '2026-02-10 01:06:19', 0);

-- ========================================================
-- 4. 管理服务数据库 (blog_admin)
-- ========================================================
DROP DATABASE IF EXISTS blog_admin;
CREATE DATABASE blog_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE blog_admin;

-- 管理员表
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `role` varchar(20) DEFAULT 'admin' COMMENT '角色',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用, 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 插入管理员数据
INSERT INTO `admin` VALUES
(6, 'admin', '$2a$10$jla2TgT9IUoXb0FyG2zQG.bV6GlQW6Lc3EIvZk9bmOvxl0uUFRy2a', 'Super Admin', 'admin@blog.com', '13800138000', NULL, 'super_admin', 1, '2026-01-17 19:47:44', '2026-01-17 19:49:00', 0);

-- 内容审核表
CREATE TABLE `content_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `content` text COMMENT '文章内容',
  `author_id` bigint NOT NULL COMMENT '作者ID',
  `author_name` varchar(100) DEFAULT NULL COMMENT '作者名称',
  `review_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态：0-待审核, 1-审核通过, 2-审核不通过',
  `review_admin_id` bigint DEFAULT NULL COMMENT '审核管理员ID',
  `review_admin_name` varchar(100) DEFAULT NULL COMMENT '审核管理员名称',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `review_comment` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容审核表';

-- 数据统计表
CREATE TABLE `data_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `new_users` int NOT NULL DEFAULT '0' COMMENT '新增用户数',
  `new_articles` int NOT NULL DEFAULT '0' COMMENT '新增文章数',
  `new_comments` int NOT NULL DEFAULT '0' COMMENT '新增评论数',
  `total_views` int NOT NULL DEFAULT '0' COMMENT '总浏览量',
  `total_likes` int NOT NULL DEFAULT '0' COMMENT '总点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据统计表';

-- 操作日志表
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `admin_id` bigint DEFAULT NULL COMMENT '管理员ID',
  `admin_name` varchar(100) DEFAULT NULL COMMENT '管理员名称',
  `operation` varchar(255) NOT NULL COMMENT '操作类型',
  `method` varchar(255) DEFAULT NULL COMMENT '请求方法',
  `params` text COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `duration` int DEFAULT NULL COMMENT '执行时长(ms)',
  `status` tinyint DEFAULT NULL COMMENT '操作状态：0-失败, 1-成功',
  `error_msg` text COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ========================================================
-- 5. 创建数据库用户并授权
-- ========================================================
-- 创建应用数据库用户（如果不存在）
CREATE USER IF NOT EXISTS 'blog_user'@'%' IDENTIFIED BY 'blog_password_123';

-- 授权
GRANT ALL PRIVILEGES ON blog_user.* TO 'blog_user'@'%';
GRANT ALL PRIVILEGES ON blog_content.* TO 'blog_user'@'%';
GRANT ALL PRIVILEGES ON blog_interaction.* TO 'blog_user'@'%';
GRANT ALL PRIVILEGES ON blog_admin.* TO 'blog_user'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

SET FOREIGN_KEY_CHECKS = 1;

-- ========================================================
-- 数据汇总
-- ========================================================
SELECT '数据库初始化完成' AS '状态';
SELECT 'blog_user' AS '数据库', COUNT(*) AS '记录数' FROM blog_user.`user`;
SELECT 'blog_content.category' AS '表', COUNT(*) AS '记录数' FROM blog_content.`category`;
SELECT 'blog_content.article' AS '表', COUNT(*) AS '记录数' FROM blog_content.`article`;
SELECT 'blog_interaction.comment' AS '表', COUNT(*) AS '记录数' FROM blog_interaction.`comment`;
SELECT 'blog_interaction.notification' AS '表', COUNT(*) AS '记录数' FROM blog_interaction.`notification`;
SELECT 'blog_interaction.message' AS '表', COUNT(*) AS '记录数' FROM blog_interaction.`message`;
SELECT 'blog_admin.admin' AS '表', COUNT(*) AS '记录数' FROM blog_admin.`admin`;

-- ========================================================
-- SQL文件生成完成
-- 数据来源：直接从MySQL数据库查询
-- 包含数据库: blog_user, blog_content, blog_interaction, blog_admin
-- 总数据量: 约80+条记录
-- ========================================================
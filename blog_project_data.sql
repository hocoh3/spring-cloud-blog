-- ========================================================
-- 基于Spring Cloud微服务架构的博客系统 - 完整数据库SQL文件
-- 包含所有表结构和示例数据
-- 生成时间: 2026-03-11
-- ========================================================

-- ========================================================
-- 1. 用户服务数据库 (blog_user)
-- ========================================================
CREATE DATABASE IF NOT EXISTS blog_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_user;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `bio` text COMMENT '个人简介',
  `role` varchar(20) DEFAULT 'user' COMMENT '角色：admin-管理员, user-普通用户',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用, 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================================
-- 2. 内容服务数据库 (blog_content)
-- ========================================================
CREATE DATABASE IF NOT EXISTS blog_content DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_content;

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` varchar(100) NOT NULL COMMENT '分类名称',
  `description` varchar(500) DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `article_count` int NOT NULL DEFAULT '0' COMMENT '文章数量',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用, 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表';

-- 文章表
CREATE TABLE IF NOT EXISTS `article` (
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
  KEY `idx_status` (`status`),
  KEY `idx_is_featured` (`is_featured`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章表';

-- 文章点赞表
CREATE TABLE IF NOT EXISTS `article_like` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章点赞表';

-- 文章收藏表
CREATE TABLE IF NOT EXISTS `article_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `article_id` bigint NOT NULL COMMENT '文章ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文章收藏表';

================================================
-- 3. 交互服务数据库 (blog_interaction)
-- ========================================================
CREATE DATABASE IF NOT EXISTS blog_interaction DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_interaction;

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
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
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 通知表
CREATE TABLE IF NOT EXISTS `notification` (
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
  KEY `idx_is_read` (`is_read`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 私信表
CREATE TABLE IF NOT EXISTS `message` (
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
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='私信表';

-- ========================================================
-- 4. 管理服务数据库 (blog_admin)
-- ========================================================
CREATE DATABASE IF NOT EXISTS blog_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_admin;

-- 管理员表
CREATE TABLE IF NOT EXISTS `admin` (
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
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表';

-- 内容审核表
CREATE TABLE IF NOT EXISTS `content_review` (
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
  KEY `idx_article_id` (`article_id`),
  KEY `idx_author_id` (`author_id`),
  KEY `idx_review_status` (`review_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容审核表';

-- 数据统计表
CREATE TABLE IF NOT EXISTS `data_statistics` (
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
  UNIQUE KEY `uk_stat_date` (`stat_date`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据统计表';

-- ========================================================
-- 5. 数据服务数据库 (blog_data) - Redis数据持久化
-- ========================================================
CREATE DATABASE IF NOT EXISTS blog_data DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_data;

-- 会话表（Redis会话持久化）
CREATE TABLE IF NOT EXISTS `session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `session_id` varchar(255) NOT NULL COMMENT '会话标识',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `user_data` json DEFAULT NULL COMMENT '用户数据',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_session_id` (`session_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- 排行榜数据表（Redis排行榜持久化）
CREATE TABLE IF NOT EXISTS `ranking` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '排名ID',
  `ranking_type` varchar(50) NOT NULL COMMENT '排行榜类型',
  `item_id` bigint NOT NULL COMMENT '项目ID',
  `item_name` varchar(255) DEFAULT NULL COMMENT '项目名称',
  `score` double NOT NULL DEFAULT '0' COMMENT '分数',
  `rank` int DEFAULT NULL COMMENT '排名',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_item` (`ranking_type`, `item_id`),
  KEY `idx_ranking_type` (`ranking_type`),
  KEY `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排行榜数据表';

-- ========================================================
-- 6. 创建数据库用户并授权
-- ========================================================
-- 创建应用数据库用户
CREATE USER IF NOT EXISTS 'blog_user'@'%' IDENTIFIED BY 'blog_password_123';

-- 授权
GRANT ALL PRIVILEGES ON blog_user.* TO 'blog_user'@'%';
GRANT ALL PRIVILEGES ON blog_content.* TO 'blog_user'@'%';
GRANT ALL PRIVILEGES ON blog_interaction.* TO 'blog_user'@'%';
GRANT ALL PRIVILEGES ON blog_admin.* TO 'blog_user'@'%';
GRANT ALL PRIVILEGES ON blog_data.* TO 'blog_user'@'%';

-- 刷新权限
FLUSH PRIVILEGES;

-- ========================================================
-- 7. 数据汇总
-- ========================================================
SELECT '数据库初始化完成' AS '状态',
       'blog_user' AS '用户数据库',
       (SELECT COUNT(*) FROM blog_user.`user`) AS '用户数',
       'blog_content' AS '内容数据库',
       (SELECT COUNT(*) FROM blog_content.`article`) AS '文章数',
       (SELECT COUNT(*) FROM blog_content.`category`) AS '分类数',
       'blog_interaction' AS '交互数据库',
       (SELECT COUNT(*) FROM blog_interaction.`comment`) AS '评论数',
       (SELECT COUNT(*) FROM blog_interaction.`notification`) AS '通知数',
       (SELECT COUNT(*) FROM blog_interaction.`message`) AS '私信数',
       'blog_admin' AS '管理数据库',
       (SELECT COUNT(*) FROM blog_admin.`admin`) AS '管理员数',
       'blog_data' AS '数据数据库',
       (SELECT COUNT(*) FROM blog_data.`ranking`) AS '排行榜数据数';

-- ========================================================
-- SQL文件生成完成
-- 生成时间: 2026-03-11
-- 包含数据库: blog_user, blog_content, blog_interaction, blog_admin, blog_data
-- 总数据量: 约100+条记录
-- ========================================================
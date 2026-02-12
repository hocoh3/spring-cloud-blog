-- Data migration script: Merge comment and notification services into interaction service
-- This script preserves all existing data

-- 1. Create interaction service database if not exists
CREATE DATABASE IF NOT EXISTS blog_interaction DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog_interaction;

-- 2. Create comment table
CREATE TABLE IF NOT EXISTS `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `content` text NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Create notification table
CREATE TABLE IF NOT EXISTS `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(255) NOT NULL,
  `content` text,
  `type` tinyint NOT NULL DEFAULT '0',
  `is_read` tinyint NOT NULL DEFAULT '0',
  `related_id` bigint DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Create message table
CREATE TABLE IF NOT EXISTS `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `sender_avatar` varchar(500) DEFAULT NULL,
  `receiver_id` bigint NOT NULL,
  `content` text NOT NULL,
  `is_read` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Migrate comment data from blog_comment.comment to blog_interaction.comment
INSERT IGNORE INTO blog_interaction.`comment` 
  (id, article_id, user_id, parent_id, content, status, create_time, update_time)
SELECT 
  id, article_id, user_id, parent_id, content, status, create_time, update_time
FROM blog_comment.`comment`;

-- 6. Migrate notification data from blog_notification.notification to blog_interaction.notification
INSERT IGNORE INTO blog_interaction.`notification`
  (id, user_id, title, content, type, is_read, related_id, create_time, update_time, status)
SELECT 
  id, user_id, title, content, type, is_read, related_id, create_time, update_time, status
FROM blog_notification.`notification`;

-- 7. Migrate message data from blog_notification.message to blog_interaction.message
INSERT IGNORE INTO blog_interaction.`message`
  (id, sender_id, sender_name, sender_avatar, receiver_id, content, is_read, create_time, update_time, status)
SELECT 
  id, sender_id, sender_name, sender_avatar, receiver_id, content, is_read, create_time, update_time, status
FROM blog_notification.`message`;

-- 8. Display migration results
SELECT 'Data migration completed!' AS 'Status';

SELECT 
  'Comment' AS 'Data Type',
  COUNT(*) AS 'Migrated Records'
FROM blog_interaction.`comment`
UNION ALL
SELECT 
  'Notification' AS 'Data Type',
  COUNT(*) AS 'Migrated Records'
FROM blog_interaction.`notification`
UNION ALL
SELECT 
  'Message' AS 'Data Type',
  COUNT(*) AS 'Migrated Records'
FROM blog_interaction.`message`;

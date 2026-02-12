# Spring Cloud 博客系统

基于 Spring Cloud 微服务架构的博客系统，包含用户管理、内容管理、评论系统、通知系统、搜索功能等完整功能。

## 项目概述

本系统采用微服务架构，包含以下服务：

- **blog-gateway**: API 网关服务
- **blog-eureka**: 服务注册与发现中心
- **blog-user-service**: 用户服务
- **blog-content-service**: 内容服务（文章、分类、标签）
- **blog-interaction-service**: 交互服务（评论、通知、私信）
- **blog-search-service**: 搜索服务（Elasticsearch）
- **blog-admin-service**: 管理后台服务

## 技术栈

### 后端
- Spring Boot 2.7.x
- Spring Cloud 2021.x
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka
- Spring Cloud OpenFeign
- MyBatis-Plus
- MySQL 8.0
- Redis 6.x
- Elasticsearch 7.x
- RabbitMQ 3.x
- JWT
- WebSocket

### 前端
- Vue.js 2.x
- Axios
- HTML5/CSS3

## 项目结构

```
spring-could-blog/
├── blog-gateway/              # API 网关
├── blog-eureka-server/         # 服务注册中心
├── blog-user-service/         # 用户服务
├── blog-content-service/      # 内容服务
├── blog-interaction-service/  # 交互服务（评论、通知、私信）
├── blog-search-service/       # 搜索服务
├── blog-admin-service/       # 管理后台
└── README.md
```

## 环境要求

- JDK 11+
- Maven 3.6+
- Docker & Docker Compose
- MySQL 8.0
- Redis 6.x
- Elasticsearch 7.x
- RabbitMQ 3.x

## 安装和配置

### 1. 克隆项目

```bash
git clone <repository-url>
cd spring-could-blog
```

### 2. Docker 环境配置

#### MySQL 配置

```bash
# 启动 MySQL 容器
docker run -d \
  --name blog-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=blog \
  -v mysql-data:/var/lib/mysql \
  mysql:8.0
```

#### Redis 配置

```bash
# 启动 Redis 容器
docker run -d \
  --name blog-redis \
  -p 6379:6379 \
  -v redis-data:/data \
  redis:6-alpine
```

#### Elasticsearch 配置

```bash
# 启动 Elasticsearch 容器
docker run -d \
  --name blog-elasticsearch \
  -p 9200:9200 \
  -p 9300:9300 \
  -e "discovery.type=single-node" \
  -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
  -v es-data:/usr/share/elasticsearch/data \
  elasticsearch:7.17.0
```

#### RabbitMQ 配置

```bash
# 启动 RabbitMQ 容器
docker run -d \
  --name blog-rabbitmq \
  -p 5672:5672 \
  -p 15672:15672 \
  -e RABBITMQ_DEFAULT_USER=guest \
  -e RABBITMQ_DEFAULT_PASS=guest \
  rabbitmq:3-management
```

### 3. 数据库初始化

连接到 MySQL 并执行初始化脚本：

```sql
-- 创建各个服务的数据库
CREATE DATABASE IF NOT EXISTS blog_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS blog_content DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS blog_interaction DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS blog_search DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS blog_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户数据库
USE blog_user;

-- 用户表
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `bio` varchar(255) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'user',
  `status` int DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 内容数据库
USE blog_content;

-- 文章表
CREATE TABLE `article` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `summary` varchar(500) DEFAULT NULL,
  `content` text,
  `cover_image` varchar(255) DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `view_count` int DEFAULT '0',
  `comment_count` int DEFAULT '0',
  `like_count` int DEFAULT '0',
  `status` int DEFAULT '0',
  `is_featured` int DEFAULT '0',
  `is_top` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分类表
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 标签表
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章点赞表
CREATE TABLE `article_like` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 文章收藏表
CREATE TABLE `article_favorite` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_article_user` (`article_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 交互数据库
USE blog_interaction;

-- 评论表
CREATE TABLE `comment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `article_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `content` text NOT NULL,
  `parent_id` bigint DEFAULT NULL,
  `status` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 通知表
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` text,
  `related_id` bigint DEFAULT NULL,
  `type` int DEFAULT '0',
  `is_read` int DEFAULT '0',
  `status` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 私信表
CREATE TABLE `message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sender_id` bigint NOT NULL,
  `sender_name` varchar(100) DEFAULT NULL,
  `sender_avatar` varchar(500) DEFAULT NULL,
  `receiver_id` bigint NOT NULL,
  `content` text NOT NULL,
  `is_read` int DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_sender_id` (`sender_id`),
  KEY `idx_receiver_id` (`receiver_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 管理员数据库
USE blog_admin;

-- 管理员表
CREATE TABLE `admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nickname` varchar(50) DEFAULT NULL,
  `role` varchar(20) DEFAULT 'admin',
  `status` int DEFAULT '1',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认管理员
INSERT INTO `admin` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 'admin', 1);
-- 默认密码: admin123
```

### 4. 启动服务

按照以下顺序启动服务：

1. **启动 Eureka 服务注册中心**
```bash
cd blog-eureka-server
mvn spring-boot:run
```

2. **启动 API 网关**
```bash
cd blog-gateway
mvn spring-boot:run
```

3. **启动各个微服务**
```bash
# 用户服务
cd blog-user-service
mvn spring-boot:run

# 内容服务
cd blog-content-service
mvn spring-boot:run

# 交互服务（评论、通知、私信）
cd blog-interaction-service
mvn spring-boot:run

# 搜索服务
cd blog-search-service
mvn spring-boot:run

# 管理后台服务
cd blog-admin-service
mvn spring-boot:run
```

## 服务端口配置

| 服务名称 | 端口 | 说明 |
|---------|------|------|
| blog-eureka-server | 8761 | 服务注册中心 |
| blog-gateway | 8070 | API 网关 |
| blog-user-service | 8000 | 用户服务 |
| blog-content-service | 8072 | 内容服务 |
| blog-interaction-service | 8073 | 交互服务（评论、通知、私信） |
| blog-search-service | 8084 | 搜索服务 |
| blog-admin-service | 9090 | 管理后台 |

## 数据库配置

系统使用多个独立的数据库，每个服务对应一个数据库：

| 服务名称 | 数据库名称 | 说明 |
|---------|-----------|------|
| blog-user-service | blog_user | 用户数据 |
| blog-content-service | blog_content | 文章、分类、标签数据 |
| blog-interaction-service | blog_interaction | 评论、通知、私信数据 |
| blog-search-service | blog_search | 搜索相关数据 |
| blog-admin-service | blog_admin | 管理员数据 |

### MySQL 连接信息
- 主机: localhost
- 端口: 3306
- 用户名: root
- 密码: 123456

### Redis 连接信息
- 主机: localhost
- 端口: 6379

### Elasticsearch 连接信息
- 主机: localhost
- 端口: 9200

### RabbitMQ 连接信息
- 主机: localhost
- 端口: 5672
- 管理界面: http://localhost:15672
- 用户名: guest
- 密码: guest

## 重要功能说明

### 1. 用户系统
- 用户注册、登录
- JWT 认证
- 个人信息管理
- 头像上传

### 2. 内容管理
- 文章 CRUD 操作
- 分类和标签管理
- 文章点赞、收藏
- 阅读量统计

### 3. 内容审核流程

**文章状态定义：**
- `status=0`: 草稿
- `status=1`: 待审核
- `status=2`: 已发布
- `status=3`: 已拒绝

**审核流程：**
1. 用户创建文章 → 自动保存为草稿（status=0）
2. 用户点击"提交审核" → 文章进入待审核状态（status=1）
3. 管理员审核：
   - **通过** → 文章发布（status=2），同步到搜索引擎
   - **拒绝** → 文章被拒绝（status=3），用户可以重新提交
4. 用户撤回审核 → 待审核文章回到草稿状态（status=0）

**相关 API：**
- `POST /api/content/articles/{id}/submit-review` - 提交审核
- `POST /api/content/articles/{id}/approve` - 审核通过
- `POST /api/content/articles/{id}/reject` - 审核拒绝
- `GET /api/content/articles/pending-review` - 获取待审核文章列表

**API 路由说明：**
- 用户服务: `http://localhost:8070/api/users/**`
- 内容服务: `http://localhost:8070/api/content/**`
- 交互服务: `http://localhost:8070/api/interaction/**`（评论、通知、私信）
- 搜索服务: `http://localhost:8070/api/search/**`
- 管理服务: `http://localhost:8070/api/admin/**`
- WebSocket: `ws://localhost:8070/ws/notification/**`

### 4. 评论系统
- 文章评论
- 评论回复
- 评论点赞

### 5. 通知系统
- 系统通知
- 评论通知
- 点赞通知
- @全体用户通知
- 私信功能

**通知类型：**
- `type=0`: 系统通知
- `type=1`: 评论通知
- `type=2`: 点赞通知
- `type=3`: @用户通知

### 6. 搜索功能
- 基于 Elasticsearch 的全文搜索
- 文章内容搜索
- 搜索结果高亮

### 7. 管理后台
- 用户管理
- 内容审核
- 数据统计
- 通知管理

## Docker 常用命令

### MySQL
```bash
# 启动 MySQL
docker start blog-mysql

# 停止 MySQL
docker stop blog-mysql

# 连接到 MySQL
docker exec -it blog-mysql mysql -uroot -p123456

# 查看日志
docker logs blog-mysql
```

### Redis
```bash
# 启动 Redis
docker start blog-redis

# 停止 Redis
docker stop blog-redis

# 连接到 Redis
docker exec -it blog-redis redis-cli

# 查看日志
docker logs blog-redis
```

### Elasticsearch
```bash
# 启动 Elasticsearch
docker start blog-elasticsearch

# 停止 Elasticsearch
docker stop blog-elasticsearch

# 查看日志
docker logs blog-elasticsearch

# 测试连接
curl http://localhost:9200
```

### RabbitMQ
```bash
# 启动 RabbitMQ
docker start blog-rabbitmq

# 停止 RabbitMQ
docker stop blog-rabbitmq

# 查看日志
docker logs blog-rabbitmq

# 访问管理界面
# 浏览器打开: http://localhost:15672
```

## 常见问题

### 1. 服务启动失败
- 检查 Eureka 服务是否正常启动
- 检查端口是否被占用
- 检查数据库连接是否正常

### 2. 数据库连接失败
- 确认 MySQL 容器是否运行
- 检查数据库连接配置
- 确认数据库是否已创建

### 3. Redis 连接失败
- 确认 Redis 容器是否运行
- 检查 Redis 连接配置

### 4. Elasticsearch 连接失败
- 确认 Elasticsearch 容器是否运行
- 检查 Elasticsearch 健康状态
- 确认索引是否已创建

### 5. RabbitMQ 连接失败
- 确认 RabbitMQ 容器是否运行
- 检查 RabbitMQ 连接配置
- 访问管理界面 http://localhost:15672 检查状态

### 6. @全体用户通知失败
- 检查 UserClient 配置
- 确认用户状态（status=1 为正常用户）
- 检查 Feign 序列化配置

### 7. 文章审核流程问题
- 确认文章状态转换逻辑
- 检查管理员权限
- 查看服务日志

## 开发规范

### 代码规范
- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- 统一异常处理
- 统一日志格式

### API 规范
- RESTful API 设计
- 统一响应格式
- 使用 JWT 认证
- 接口文档使用 Swagger

### 数据库规范
- 表名使用小写加下划线
- 字段名使用小写加下划线
- 必须有 create_time 和 update_time
- 必须有主键和索引

## 联系方式

如有问题，请联系项目维护者。

## 许可证

MIT License

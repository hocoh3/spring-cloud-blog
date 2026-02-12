@echo off
chcp 65001 >nul
echo 正在启动 blog-user-service (包含前端服务)...
echo 用户服务地址: http://localhost:8000/
echo 前端页面地址: http://localhost:8000/
echo 按 Ctrl+C 停止服务
echo.
cd blog-user-service
mvn spring-boot:run
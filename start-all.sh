#!/bin/bash

# EIMS 一键启动脚本
# 需要: Docker, Docker Compose

echo "===== EIMS 企业物资管理系统 - 一键启动 ====="
echo ""

# 检查 Docker
docker --version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "错误: Docker 未安装"
    exit 1
fi

docker-compose --version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "错误: Docker Compose 未安装"
    exit 1
fi

echo "Docker 版本: $(docker --version)"
echo "Docker Compose 版本: $(docker-compose --version)"
echo ""

# 检查端口
echo "检查端口占用..."
for port in 3306 8080 3000; do
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo "警告: 端口 $port 已被占用"
    fi
done
echo ""

# 启动服务
echo "启动服务..."
docker-compose up -d

echo ""
echo "===== 启动完成 ====="
echo ""
echo "服务地址:"
echo "  前端: http://localhost:3000"
echo "  后端: http://localhost:8080/api"
echo "  数据库: localhost:3306"
echo ""
echo "测试账号:"
echo "  用户名: admin"
echo "  密码: 123456"
echo ""
echo "停止服务: docker-compose down"

#!/bin/bash

# EIMS 后端启动脚本
# 需要先安装 MySQL 8.0

echo "===== EIMS 企业物资管理系统 - 后端启动 ====="

# 检查 MySQL
echo "检查 MySQL 连接..."
mysql -h localhost -u root -proot123 -e "USE eims;" 2>/dev/null
if [ $? -ne 0 ]; then
    echo "错误: 无法连接 MySQL，请确保:"
    echo "  1. MySQL 8.0 已安装并运行"
    echo "  2. 用户名: root, 密码: root123"
    echo "  3. 已执行 sql/init.sql 初始化数据库"
    exit 1
fi

echo "MySQL 连接正常"
echo ""
echo "启动后端服务..."
echo "  API 地址: http://localhost:8080/api"
echo "  健康检查: http://localhost:8080/api/health"
echo ""

cd eims-backend
mvn spring-boot:run

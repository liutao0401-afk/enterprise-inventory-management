#!/bin/bash

# EIMS 前端启动脚本
# 需要先安装 Node.js 18+

echo "===== EIMS 企业物资管理系统 - 前端启动 ====="

# 检查 Node.js
node --version > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "错误: Node.js 未安装，请先安装 Node.js 18+"
    exit 1
fi

echo "Node.js 版本: $(node --version)"
echo "npm 版本: $(npm --version)"
echo ""

# 安装依赖
echo "安装依赖..."
cd eims-frontend
npm install

echo ""
echo "启动前端服务..."
echo "  前端地址: http://localhost:3000"
echo "  API 代理: http://localhost:8080/api"
echo ""

npm run dev

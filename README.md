# Enterprise Inventory Management System (EIMS)

> 基于 RuoYi-Vue-Pro + Vue Vben Admin 的企业级物资管理系统

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Spring Boot 3.2 + MyBatis-Plus + Redis + RocketMQ |
| 前端 | Vue3 + Vben Admin + Ant Design Vue + ECharts |
| 数据库 | MySQL 8.0 |
| 部署 | Docker + Kubernetes |

## 项目结构

```
enterprise-inventory-management/
├── docs/               # 设计文档
├── sql/                # 数据库脚本
├── eims-backend/       # 后端服务（多模块 Maven 项目）
└── eims-frontend/      # 前端 Vue3 项目
```

## 快速开始

### 后端启动

```bash
cd eims-backend
docker-compose up -d   # 启动 MySQL + Redis + RocketMQ
mvn spring-boot:run    # 启动后端服务
```

### 前端启动

```bash
cd eims-frontend
pnpm install
pnpm dev
```

## 文档

- [SPEC.md](./docs/SPEC.md) - 完整设计规范
- [API 文档](./docs/openapi.md) - 内部 API 接口
- [部署指南](./docs/deployment.md) - 容器化部署

## License

MIT

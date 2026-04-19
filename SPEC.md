# 企业物资管理系统 (EIMS) - 简化版技术方案

> 版本：v2.0.0（简化版）
> 日期：2026-04-19
> 状态：规划中

---

## 一、设计原则

**Keep It Simple** — 能用 MySQL 解决的不用其他组件

---

## 二、技术栈（极简版）

| 层级 | 技术选型 |
|------|---------|
| 后端 | Spring Boot 3 + Spring Data JPA + MySQL 8 |
| 前端 | Vue3 + Element Plus + Vite |
| 数据库 | MySQL 8 单机 |
| 部署 | Docker Compose（1台服务器） |

**对比原方案：去掉所有非必要组件**
- 原：微服务 + Redis + RocketMQ + ES + PowerJob + MinIO + K8s
- 新：单体 + MySQL

---

## 三、系统架构

```
┌─────────────────────────────────────────┐
│              浏览器 (Chrome)              │
└─────────────────┬───────────────────────┘
                  │ HTTP
┌─────────────────▼───────────────────────┐
│          Vue3 前端 (Nginx)              │
│     http://内网IP:80                    │
└─────────────────┬───────────────────────┘
                  │ REST API (JSON)
┌─────────────────▼───────────────────────┐
│       Spring Boot 单体应用              │
│     Jar 包 + MySQL 驱动                │
└─────────────────┬───────────────────────┘
                  │ JDBC
┌─────────────────▼───────────────────────┐
│          MySQL 8 数据库                │
└─────────────────────────────────────────┘
```

---

## 四、项目结构

```
enterprise-inventory-management/
├── docs/
│   └── SPEC.md              # 本文档
├── sql/
│   └── init.sql             # 数据库初始化
├── eims-backend/
│   ├── pom.xml              # Maven 配置
│   └── src/main/
│       ├── java/com/eims/
│       │   ├── EimsApplication.java
│       │   ├── controller/   # 控制器
│       │   ├── service/     # 业务层
│       │   ├── repository/   # 数据层 (JPA)
│       │   ├── entity/      # 实体类
│       │   └── dto/         # 数据传输对象
│       └── resources/
│           └── application.yml
├── eims-frontend/          # Vue3 项目 (待创建)
├── docker/
│   └── docker-compose.yml   # 部署配置
└── README.md
```

---

## 五、数据库设计（8张核心表）

```sql
-- 1. 物资分类
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(128) NOT NULL,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. 物资主表
CREATE TABLE material (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(256) NOT NULL,
    category_id BIGINT,
    unit VARCHAR(16),
    spec VARCHAR(256),
    safety_stock DECIMAL(12,2) DEFAULT 0,
    current_stock DECIMAL(12,2) DEFAULT 0,
    price DECIMAL(12,2) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. 供应商
CREATE TABLE supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(256) NOT NULL,
    contact VARCHAR(64),
    phone VARCHAR(32),
    address VARCHAR(512),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. 采购需求
CREATE TABLE requirement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL,
    material_id BIGINT NOT NULL,
    quantity DECIMAL(12,2) NOT NULL,
    purpose VARCHAR(256),
    status VARCHAR(16) DEFAULT 'PENDING',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. 采购订单
CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL,
    supplier_id BIGINT,
    amount DECIMAL(12,2) DEFAULT 0,
    status VARCHAR(16) DEFAULT 'DRAFT',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 6. 采购订单明细
CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity DECIMAL(12,2) NOT NULL,
    price DECIMAL(12,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 7. 仓库
CREATE TABLE warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(256),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 8. 库存流水
CREATE TABLE inventory_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    type VARCHAR(16),
    quantity DECIMAL(12,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 9. 质检记录
CREATE TABLE qc_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id BIGINT,
    order_id BIGINT,
    result VARCHAR(16),
    quantity DECIMAL(12,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 10. 系统用户
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    real_name VARCHAR(64),
    phone VARCHAR(32),
    status VARCHAR(16) DEFAULT 'ACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 六、API 接口设计

| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /api/auth/login` | 登录 |
| `POST /api/auth/logout` | 登出 |
| `GET /api/category` | 分类列表 |
| `POST /api/category` | 新增分类 |
| `GET /api/material` | 物资列表 |
| `POST /api/material` | 新增物资 |
| `GET /api/material/{id}` | 物资详情 |
| `PUT /api/material/{id}` | 修改物资 |
| `DELETE /api/material/{id}` | 删除物资 |
| `GET /api/supplier` | 供应商列表 |
| `POST /api/supplier` | 新增供应商 |
| `GET /api/requirement` | 需求列表 |
| `POST /api/requirement` | 创建需求 |
| `PUT /api/requirement/{id}/approve` | 审批需求 |
| `GET /api/order` | 订单列表 |
| `POST /api/order` | 创建订单 |
| `POST /api/order/{id}/confirm` | 确认订单 |
| `GET /api/warehouse` | 仓库列表 |
| `POST /api/warehouse` | 新增仓库 |
| `POST /api/inventory/in` | 入库操作 |
| `POST /api/inventory/out` | 出库操作 |
| `GET /api/inventory/log` | 库存流水 |
| `POST /api/qc` | 提交质检 |
| `GET /api/qc` | 质检记录 |

---

## 七、Docker 部署

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    container_name: eims-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_PASSWORD}
      MYSQL_DATABASE: eims
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql/init.sql:/docker-entrypoint-initdb.d/init.sql
    restart: unless-stopped

  backend:
    build: ./eims-backend
    container_name: eims-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/eims
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
    depends_on:
      - mysql
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    container_name: eims-nginx
    ports:
      - "80:80"
    volumes:
      - ./eims-frontend/dist:/usr/share/nginx/html
      - ./docker/nginx.conf:/etc/nginx/conf.d/default.conf
    depends_on:
      - backend
    restart: unless-stopped

volumes:
  mysql_data:
```

---

## 八、实施计划（7周）

### 第一期：基础功能（4周）

| 周数 | 模块 | 功能 |
|------|------|------|
| 第1周 | 系统管理 | 登录/登出、用户CRUD |
| 第2周 | 物资管理 | 物资分类、物资主档 CRUD |
| 第3周 | 采购需求 | 需求申请、需求审批 |
| 第4周 | 仓库管理 | 入库、出库、库存查询 |

### 第二期：增强功能（3周）

| 周数 | 模块 | 功能 |
|------|------|------|
| 第5周 | 采购订单 | 订单管理、供应商管理 |
| 第6周 | 质检+预警 | 质检记录、库存预警提醒 |
| 第7周 | 导入+报表 | Excel批量导入、基础报表 |

---

## 九、服务器要求

| 配置 | 最低 | 推荐 |
|------|------|------|
| CPU | 2核 | 4核 |
| 内存 | 4GB | 8GB |
| 硬盘 | 50GB | 100GB |
| 系统 | Ubuntu 22.04 | Ubuntu 22.04 |

---

## 十、快速启动

```bash
# 1. 克隆代码
git clone https://github.com/liutao0401-afk/enterprise-inventory-management.git
cd enterprise-inventory-management

# 2. 配置数据库密码
export MYSQL_PASSWORD=your_password_here

# 3. 启动所有服务
docker-compose up -d

# 4. 访问
# 前端: http://localhost:80
# 后端API: http://localhost:8080
# 数据库: localhost:3306
```

---

## 十一、技术对比

| 对比项 | 原方案 (v1) | 简化版 (v2) |
|--------|-------------|-------------|
| 服务数量 | 8个 | 1个 |
| 中间件 | 6种 | 0种 |
| 表数量 | 18张 | 10张 |
| 接口数量 | 18个 | 24个 |
| 服务器要求 | K8s集群 | 1台4核8G |
| 开发周期 | 18周 | 7周 |

---

*本文档为简化版技术方案，随着项目推进将持续迭代更新。*
*最后更新：2026-04-19*

# EIMS 企业物资管理系统 - 完整技术规格说明书

> 版本：v2.0.0
> 日期：2026-04-19
> 状态：需求确认完成

---

## 一、项目概述

### 1.1 项目背景

企业物资管理是工业制造、化工、港口物流等行业的核心运营环节。传统系统存在数据孤岛、重复采购、库存失控、报表滞后等问题。

**目标**：构建一套简单易用的企业级物资管理系统，覆盖从需求提出→采购执行→仓库管理→数据分析的全链路流程。

### 1.2 设计原则

**Keep It Simple** — 能用 MySQL 解决的不用其他组件

### 1.3 技术选型（极简版）

| 层级 | 技术选型 |
|------|---------|
| 后端 | Spring Boot 3 + Spring Data JPA + MySQL 8 |
| 前端 | Vue3 + Element Plus + Vite |
| 数据库 | MySQL 8 单机 |
| 部署 | Docker Compose |

**不用的组件**：Redis、RocketMQ、Elasticsearch、微服务架构（内部系统无需这些）

---

## 二、系统模块总览

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              EIMS 系统模块                                   │
├─────────────────────────────────────────────────────────────────────────────┤
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐               │
│   │  系统管理   │    │  物资管理   │    │ 采购需求   │               │
│   │  登录/用户  │    │  分类/主档  │    │  申请/审批  │               │
│   │  角色/权限  │    │             │    │  重复检测  │               │
│   └─────────────┘    └─────────────┘    └──────┬──────┘               │
│                                                  │                        │
│                                                  ▼                        │
│   ┌─────────────┐    ┌─────────────┐    ┌─────────────┐               │
│   │  质检管理   │◀───│  仓库管理   │◀───│ 采购订单   │               │
│   │  到货质检   │    │  入库/出库  │    │  订单管理   │               │
│   └─────────────┘    └──────┬──────┘    └─────────────┘               │
│                              │                                         │
│                              ▼                                         │
│                    ┌─────────────────┐                                 │
│                    │    预警系统     │                                 │
│                    │ 库存预警/飞书推送│                                 │
│                    └─────────────────┘                                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 三、核心业务流程

### 3.1 采购全链路流程

```
需求部门 ──▶ 审批人 ──▶ 采购员 ──▶ 供应商 ──▶ 仓库管理员
                                          │
                                          ▼
                               ┌──────────────────────┐
                               │    到货登记         │
                               └──────────┬─────────┘
                                          │
                               ┌──────────▼─────────┐
                               │    提交质检        │
                               └──────────┬─────────┘
                                          │
                               ┌──────────▼─────────┐
                               │  合格入库/不合格退货 │
                               └──────────┬─────────┘
                                          │
                               ┌──────────▼─────────┐
                               │    通知领用        │
                               └──────────┬─────────┘
                                          │
                               ┌──────────▼─────────┐
                               │    领用出库        │
                               └──────────────────────┘
```

### 3.2 需求状态机

```
PENDING ──▶ APPROVED ──▶ PURCHASING ──▶ COMPLETED
(待审批)    (已审批)    (采购中)      (已完成)
    │
    ▼
REJECTED / CANCELLED
(已驳回/已取消)
```

### 3.3 订单状态机

```
DRAFT ──▶ CONFIRMED ──▶ PARTIAL_SHIPPED ──▶ SHIPPED ──▶ RECEIVED
(草稿)    (已确认)      (部分发货)       (已发货)     (已收货)
                                                            │
                                                            ▼
                                                       COMPLETED
                                                       (已完成)
```

---

## 四、详细功能说明

### 4.1 系统管理模块

**功能**：
- 用户登录/登出（Token 认证）
- 用户管理（CRUD）
- 角色管理（系统管理员/采购主管/采购员/仓库管理员/质检员/财务/需求部门）
- 权限控制（菜单权限/操作权限/数据权限）

**用户角色**：

| 角色 | 职责 |
|------|------|
| 系统管理员 | 用户管理、权限配置 |
| 采购主管 | 审批需求、管理供应商 |
| 采购员 | 制作订单、跟踪采购 |
| 仓库管理员 | 入库/出库操作 |
| 质检员 | 执行质检 |
| 财务 | 发票核对 |
| 需求部门 | 提交需求、领用物资 |

### 4.2 物资管理模块

**物资分类**：
- 树形结构（父分类/子分类）
- 支持新增/编辑/删除/排序

**物资主档字段**：

| 字段 | 说明 |
|------|------|
| 物资编码 | 唯一标识，如 MAT-001 |
| 物资名称 | 商品全称 |
| 所属分类 | 关联分类表 |
| 规格型号 | 详细规格 |
| 单位 | 个/箱/千克等 |
| 安全库存 | 低于此值触发预警 |
| 最低库存 | 紧急补货点 |
| 当前库存 | 实时库存数量 |
| 参考单价 | 采购参考价 |

### 4.3 采购需求模块（核心）

**需求提交流程**：
1. 需求部门填写：物资/数量/期望日期/用途
2. 系统自动检测重复（可选择 AI 或系统逻辑）
3. 显示检测结果，用户选择：合并/忽略/取消
4. 提交成功，等待审批

**审批流程**：
- 金额 < 1万 → 部门主管一级审批
- 1万 ≤ 金额 < 10万 → 采购主管二级审批
- 金额 ≥ 10万 → 总经理三级审批

**重复检测功能**：

| 模式 | 说明 |
|------|------|
| AI智能检测 | 调用AI大模型分析语义相似度 |
| 系统逻辑检测 | SQL精确匹配+关键词模糊匹配 |

### 4.4 采购订单模块

**功能**：
- 根据需求单生成采购订单
- 订单管理（新增/编辑/确认/取消）
- 供应商管理（CRUD + 绩效统计）
- 订单状态跟踪

**供应商绩效**：
- 交货准时率 = 准时交货次数 / 总交货次数
- 质量合格率 = 合格数量 / 到货数量
- 综合评级 = (准时率×0.4) + (合格率×0.6)

### 4.5 仓库管理模块

**入库流程**：
1. 到货登记（关联采购订单）
2. 提交质检
3. 质检合格后入库
4. 系统自动更新库存数量
5. 生成库存流水记录
6. 通知需求部门可领用

**出库流程**：
1. 部门领用申请（可选）
2. 仓库办理出库
3. 确认领用数量和领取人
4. 系统自动扣减库存
5. 生成出库流水记录

**库存流水类型**：
- IN（采购入库）
- OUT（领用出库）
- TRANSFER（库间调拨）
- ADJUST（盘点调整）

### 4.6 质检管理模块

**流程**：
1. 到货后自动/手动提交质检
2. 质检员执行检验（抽样/外观/尺寸/功能）
3. 记录合格数量/不合格数量/原因
4. 判定结果：PASS/FAIL
5. 合格 → 入库；不合格 → 退货处理

**质检记录**：
- 批次号、抽样数量、检验项目
- 合格数、不合格数、不合格原因
- 支持上传质检报告附件

### 4.7 预警系统模块

**预警类型**：

| 类型 | 触发条件 | 级别 |
|------|---------|------|
| 安全库存预警 | 库存 ≤ 安全库存 | INFO |
| 紧急补货预警 | 库存 ≤ 最低库存 | WARNING |
| 断货预警 | 库存 = 0 | CRITICAL |

**通知渠道**：飞书机器人 Webhook 推送

**预警触发场景**：
- 库存低于安全库存 → 通知采购主管 + 仓库管理员
- 质检不合格 → 通知采购主管 + 供应商管理
- 采购到货 → 通知需求部门 + 质检员

**防预警风暴**：同一物资同类型预警，4小时内不重复通知

### 4.8 数据导入模块

**支持导入**：
- 物资主档（批量新增物资）
- 供应商信息
- 采购需求（批量提交）

**导入流程**：
1. 下载标准模板
2. 按格式填写
3. 上传Excel
4. 系统校验格式
5. 逐行业务处理
6. 返回导入结果

**导入状态**：UPLOADED → PARSING → FINISHED / PARSE_FAIL

### 4.9 报表中心

**内置报表（9类）**：

| # | 报表名称 | 说明 |
|---|---------|------|
| 1 | 库存台账 | 实时库存汇总，支持按仓库/分类筛选 |
| 2 | 采购汇总表 | 按月/季度统计采购金额 |
| 3 | 到货及时率 | 供应商准时交货比例分析 |
| 4 | 质检合格率 | 各物资/供应商质量分析 |
| 5 | 物资周转率 | 库存周转天数分析 |
| 6 | 需求漏斗 | 申请→审批→采购→到货全链路 |
| 7 | 供应商绩效 | 交货/质量/价格综合评分 |
| 8 | 出入库流水 | 明细流水账 |
| 9 | 在途订单 | 已下单未到货物资汇总 |

**导出格式**：Excel / PDF / CSV

---

## 五、飞书集成

### 5.1 功能列表

| 功能 | 说明 |
|------|------|
| 审批通知 | 需求提交/通过/驳回时通知 |
| 预警推送 | 库存低于安全库存时推送 |
| 到货通知 | 采购到货后通知需求部门 |

### 5.2 配置项

```yaml
feishu:
  enabled: true
  webhook_url: https://open.feishu.cn/open-apis/bot/v2/hook/xxxxx
  notify_events:
    - requirement_submit
    - requirement_approve
    - requirement_reject
    - stock_alert
    - goods_arrived
```

---

## 六、AI 辅助重复检测

### 6.1 配置项

```yaml
duplicate_check:
  enabled: true
  default_mode: SYSTEM  # SYSTEM / AI
  ai:
    provider: minimax  # openai / minimax / custom
    api_key: ${AI_API_KEY}
    model: minimax-01
```

### 6.2 AI 检测流程

```
用户提交需求
      │
      ▼
系统提取：物资名称、规格型号、采购用途
      │
      ▼
调用AI大模型，判断是否与30天内历史需求重复
      │
      ▼
返回结果：is_duplicate / similar_items / reason
      │
      ▼
用户选择：合并到已有需求 / 忽略提醒 / 取消申请
```

### 6.3 AI Prompt

```
你是一个企业物资管理专家。
判断新的采购需求是否与历史需求重复。

历史需求（30天内）：
{material_list}

新需求：
- 物资名称: {new_name}
- 规格型号: {new_spec}
- 采购用途: {new_purpose}

请返回JSON：
{
  "is_duplicate": true/false,
  "similarity_score": 0.0-1.0,
  "similar_items": [{"name": "xxx", "reason": "原因"}],
  "reason": "判断理由"
}
```

---

## 七、数据库设计（10张核心表）

```sql
-- 1. 系统用户
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    real_name VARCHAR(64),
    phone VARCHAR(32),
    role_id BIGINT,
    status VARCHAR(16) DEFAULT 'ACTIVE',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. 角色表
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(64) NOT NULL,
    role_code VARCHAR(64) NOT NULL UNIQUE,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. 物资分类
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    parent_id BIGINT DEFAULT 0,
    name VARCHAR(128) NOT NULL,
    sort INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 4. 物资主表
CREATE TABLE material (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(256) NOT NULL,
    category_id BIGINT,
    unit VARCHAR(16),
    spec VARCHAR(256),
    safety_stock DECIMAL(12,2) DEFAULT 0,
    min_stock DECIMAL(12,2) DEFAULT 0,
    current_stock DECIMAL(12,2) DEFAULT 0,
    price DECIMAL(12,2) DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. 供应商
CREATE TABLE supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(256) NOT NULL,
    contact VARCHAR(64),
    phone VARCHAR(32),
    address VARCHAR(512),
    level VARCHAR(16) DEFAULT 'C',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 6. 采购需求
CREATE TABLE requirement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    material_id BIGINT NOT NULL,
    quantity DECIMAL(12,2) NOT NULL,
    purpose VARCHAR(256),
    status VARCHAR(16) DEFAULT 'PENDING',
    check_mode VARCHAR(16) DEFAULT 'SYSTEM',
    ai_check_result JSON,
    create_user_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 7. 采购订单
CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(64) NOT NULL UNIQUE,
    supplier_id BIGINT,
    amount DECIMAL(12,2) DEFAULT 0,
    status VARCHAR(16) DEFAULT 'DRAFT',
    create_user_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 8. 采购订单明细
CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity DECIMAL(12,2) NOT NULL,
    price DECIMAL(12,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 9. 仓库
CREATE TABLE warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(256),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 10. 库存流水
CREATE TABLE inventory_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id BIGINT NOT NULL,
    warehouse_id BIGINT,
    type VARCHAR(16),
    quantity DECIMAL(12,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 11. 质检记录
CREATE TABLE qc_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    material_id BIGINT,
    order_id BIGINT,
    batch_no VARCHAR(64),
    quantity DECIMAL(12,2),
    result VARCHAR(16),
    qualified_qty DECIMAL(12,2),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

---

## 八、API 接口设计

### 8.1 系统管理

| 接口 | 方法 | 说明 |
|------|------|------|
| `POST /api/auth/login` | 登录 |
| `POST /api/auth/logout` | 登出 |
| `GET /api/user` | 用户列表 |
| `POST /api/user` | 新增用户 |
| `PUT /api/user/{id}` | 修改用户 |
| `DELETE /api/user/{id}` | 删除用户 |

### 8.2 物资管理

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/category` | 分类列表 |
| `POST /api/category` | 新增分类 |
| `GET /api/material` | 物资列表 |
| `POST /api/material` | 新增物资 |
| `GET /api/material/{id}` | 物资详情 |
| `PUT /api/material/{id}` | 修改物资 |
| `DELETE /api/material/{id}` | 删除物资 |

### 8.3 采购需求

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/requirement` | 需求列表 |
| `POST /api/requirement` | 创建需求 |
| `POST /api/requirement/check-duplicate` | 检测重复 |
| `PUT /api/requirement/{id}/approve` | 审批通过 |
| `PUT /api/requirement/{id}/reject` | 审批驳回 |

### 8.4 采购订单

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/order` | 订单列表 |
| `POST /api/order` | 创建订单 |
| `PUT /api/order/{id}/confirm` | 确认订单 |
| `GET /api/supplier` | 供应商列表 |
| `POST /api/supplier` | 新增供应商 |

### 8.5 仓库管理

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/warehouse` | 仓库列表 |
| `POST /api/warehouse` | 新增仓库 |
| `POST /api/inventory/in` | 入库操作 |
| `POST /api/inventory/out` | 出库操作 |
| `GET /api/inventory/log` | 库存流水 |

### 8.6 质检

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/qc` | 质检列表 |
| `POST /api/qc` | 提交质检 |

### 8.7 系统配置

| 接口 | 方法 | 说明 |
|------|------|------|
| `GET /api/setting/ai-config` | 获取AI配置 |
| `POST /api/setting/ai-config` | 配置AI参数 |
| `GET /api/setting/feishu-config` | 获取飞书配置 |
| `POST /api/setting/feishu-config` | 配置飞书 |
| `POST /api/feishu/test` | 测试飞书连接 |

---

## 九、实施计划（7周）

### 第一期：基础功能（4周）

| 周 | 模块 | 功能 |
|----|------|------|
| 第1周 | 系统管理 | 登录、用户CRUD、角色权限 |
| 第2周 | 物资管理 | 分类管理、物资主档CRUD |
| 第3周 | 采购需求 | 需求申请、重复检测、审批流程 |
| 第4周 | 仓库管理 | 入库、出库、库存查询 |

### 第二期：增强功能（3周）

| 周 | 模块 | 功能 |
|----|------|------|
| 第5周 | 采购订单 | 订单管理、供应商管理 |
| 第6周 | 质检+预警 | 质检记录、库存预警、飞书推送 |
| 第7周 | 导入+报表 | Excel导入、9类报表 |

---

## 十、Docker 部署

```yaml
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
    depends_on:
      - backend
    restart: unless-stopped

volumes:
  mysql_data:
```

### 启动命令

```bash
cd /opt/eims
docker-compose up -d
```

### 访问地址

- 前端：http://localhost:80
- 后端API：http://localhost:8080
- 数据库：localhost:3306

---

## 十一、技术对比

| 对比项 | 原方案 (v1) | 简化版 (v2) |
|--------|-------------|-------------|
| 架构 | 微服务 (8个) | 单体应用 |
| 中间件 | Redis/RocketMQ/ES等6种 | 0种 |
| 表数量 | 18张 | 11张 |
| 接口数量 | 18个 | 30+个 |
| 服务器要求 | K8s集群 | 1台4核8G |
| 开发周期 | 18周 | 7周 |

---

*本文档为 EIMS 企业物资管理系统完整技术规格说明书*
*最后更新：2026-04-19*

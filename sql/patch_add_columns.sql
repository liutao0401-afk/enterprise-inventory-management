-- EIMS 数据库补丁 - 添加多租户和逻辑删除字段
-- 日期: 2026-04-21

USE eims;

-- 为所有业务表添加 tenant_id 和 deleted 字段

-- sys_role 表
ALTER TABLE sys_role ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE sys_role ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- sys_user 表
ALTER TABLE sys_user ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE sys_user ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- category 表
ALTER TABLE category ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE category ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- material 表
ALTER TABLE material ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE material ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- supplier 表
ALTER TABLE supplier ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE supplier ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- warehouse 表
ALTER TABLE warehouse ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE warehouse ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- requirement 表
ALTER TABLE requirement ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE requirement ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- purchase_order 表
ALTER TABLE purchase_order ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE purchase_order ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- purchase_order_item 表
ALTER TABLE purchase_order_item ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE purchase_order_item ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- inventory_log 表
ALTER TABLE inventory_log ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE inventory_log ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- qc_record 表
ALTER TABLE qc_record ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE qc_record ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- notification_config 表
ALTER TABLE notification_config ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE notification_config ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- ai_config 表
ALTER TABLE ai_config ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE ai_config ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

-- system_config 表
ALTER TABLE system_config ADD COLUMN tenant_id BIGINT DEFAULT 1 COMMENT '租户ID' AFTER id;
ALTER TABLE system_config ADD COLUMN deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记' AFTER tenant_id;

SELECT '数据库字段添加完成!' AS message;

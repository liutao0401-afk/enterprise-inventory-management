-- EIMS 企业物资管理系统 - 数据库初始化脚本
-- 版本: v2.0.0
-- 日期: 2026-04-19

-- 创建数据库
CREATE DATABASE IF NOT EXISTS eims DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE eims;

-- ============================================
-- 1. 角色表 (sys_role)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(64) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(256) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 插入默认角色
INSERT INTO sys_role (role_name, role_code, description) VALUES
('系统管理员', 'ADMIN', '系统管理，拥有所有权限'),
('采购主管', 'PURCHASE_MANAGER', '采购管理，审批采购需求'),
('采购员', 'PURCHASER', '执行采购，制作订单'),
('仓库管理员', 'WAREHOUSE_ADMIN', '仓库管理，入库出库操作'),
('质检员', 'QC_INSPECTOR', '质量检验'),
('财务', 'FINANCE', '财务结算，发票核对'),
('需求部门', 'DEPARTMENT', '提交需求，领用物资');

-- ============================================
-- 2. 用户表 (sys_user)
-- ============================================
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(256) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(64) COMMENT '真实姓名',
    phone VARCHAR(32) COMMENT '手机号',
    email VARCHAR(128) COMMENT '邮箱',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_name VARCHAR(128) COMMENT '所属部门',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常，INACTIVE-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (role_id) REFERENCES sys_role(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认用户 (密码: 123456)
INSERT INTO sys_user (username, password, real_name, phone, role_id, dept_name) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', '13800138000', 1, '信息中心'),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '采购主管', '13800138001', 2, '采购部'),
('purchaser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '张三', '13800138002', 3, '采购部'),
('warehouse', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '李四', '13800138003', 4, '仓储部'),
('qc', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '王五', '13800138004', 5, '质检部'),
('finance', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '赵六', '13800138005', 6, '财务部'),
('dept_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '钱七', '13800138006', 7, '生产部');

-- ============================================
-- 3. 物资分类表 (category)
-- ============================================
CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID，0为顶级',
    name VARCHAR(128) NOT NULL COMMENT '分类名称',
    code VARCHAR(64) COMMENT '分类编码',
    sort INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资分类表';

-- 插入默认分类
INSERT INTO category (id, parent_id, name, code, sort) VALUES
(1, 0, '原材料', 'RAW', 1),
(2, 1, '金属材料', 'RAW_METAL', 1),
(3, 2, '钢材', 'RAW_METAL_STEEL', 1),
(4, 2, '铝材', 'RAW_METAL_ALUMINUM', 2),
(5, 1, '化工原料', 'RAW_CHEMICAL', 2),
(6, 0, '零部件', 'PARTS', 2),
(7, 6, '标准件', 'PARTS_STANDARD', 1),
(8, 7, '轴承', 'PARTS_STANDARD_BEARING', 1),
(9, 7, '螺栓', 'PARTS_STANDARD_BOLT', 2),
(10, 6, '专用件', 'PARTS_SPECIAL', 2),
(11, 0, '包装材料', 'PACKAGING', 3),
(12, 0, 'MRO备件', 'MRO', 4),
(13, 0, '办公耗材', 'OFFICE', 5);

-- ============================================
-- 4. 物资主表 (material)
-- ============================================
CREATE TABLE IF NOT EXISTS material (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '物资ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '物资编码',
    name VARCHAR(256) NOT NULL COMMENT '物资名称',
    category_id BIGINT COMMENT '分类ID',
    unit VARCHAR(16) DEFAULT '个' COMMENT '单位',
    spec VARCHAR(256) COMMENT '规格型号',
    safety_stock DECIMAL(12,2) DEFAULT 0 COMMENT '安全库存',
    min_stock DECIMAL(12,2) DEFAULT 0 COMMENT '最低库存',
    current_stock DECIMAL(12,2) DEFAULT 0 COMMENT '当前库存',
    price DECIMAL(12,2) DEFAULT 0 COMMENT '参考单价',
    supplier_id BIGINT COMMENT '主要供应商ID',
    warehouse_id BIGINT COMMENT '存放仓库ID',
    location VARCHAR(128) COMMENT '库位',
    description VARCHAR(512) COMMENT '备注',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常，INACTIVE-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES category(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资主表';

-- 插入测试物资数据
INSERT INTO material (code, name, category_id, unit, spec, safety_stock, min_stock, current_stock, price) VALUES
('MAT-001', '深沟球轴承 6205-2RS', 8, '个', '内径25mm 外径52mm 厚度15mm', 50, 20, 100, 8.50),
('MAT-002', '螺栓 M10x50', 9, '个', '10.9级高强度', 500, 200, 800, 0.85),
('MAT-003', '钢材 Q235 10mm', 3, '千克', '10mm厚度 板材', 1000, 500, 2000, 4.20),
('MAT-004', '铝材 6061 5mm', 4, '千克', '5mm厚度 板材', 500, 200, 600, 22.00),
('MAT-005', '润滑油 WD-40', 5, '瓶', '400ml/瓶', 30, 10, 50, 35.00),
('MAT-006', '包装箱 30x30x30', 11, '个', '纸箱 三层加厚', 200, 100, 300, 3.50),
('MAT-007', '手套 乳胶', 13, '双', '一次性使用', 500, 200, 1000, 0.80),
('MAT-008', '轴承 6206-2RS', 8, '个', '内径30mm 外径62mm 厚度16mm', 40, 15, 60, 12.00);

-- ============================================
-- 5. 供应商表 (supplier)
-- ============================================
CREATE TABLE IF NOT EXISTS supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '供应商ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '供应商编码',
    name VARCHAR(256) NOT NULL COMMENT '供应商名称',
    contact VARCHAR(64) COMMENT '联系人',
    phone VARCHAR(32) COMMENT '联系电话',
    email VARCHAR(128) COMMENT '邮箱',
    address VARCHAR(512) COMMENT '地址',
    main_product VARCHAR(256) COMMENT '主营产品',
    level VARCHAR(16) DEFAULT 'B' COMMENT '评级：A/B/C/D',
    on_time_rate DECIMAL(5,2) DEFAULT 0 COMMENT '准时交货率%',
    qualify_rate DECIMAL(5,2) DEFAULT 0 COMMENT '质量合格率%',
    bank_name VARCHAR(128) COMMENT '开户银行',
    bank_account VARCHAR(64) COMMENT '银行账号',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常，INACTIVE-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- 插入测试供应商
INSERT INTO supplier (code, name, contact, phone, email, address, main_product, level, on_time_rate, qualify_rate) VALUES
('SUP-001', '上海轴承有限公司', '陈总', '021-88880001', 'chen@shbearing.com', '上海市嘉定区工业区', '轴承系列', 'A', 95.5, 98.2),
('SUP-002', '浙江标准件厂', '李总', '0571-88880002', 'li@zjstandard.com', '杭州市萧山区', '螺栓螺母系列', 'A', 92.0, 96.5),
('SUP-003', '宝钢物资贸易', '王总', '021-88880003', 'wang@baosteel.com', '上海市宝山区', '钢材铝材', 'A', 98.0, 99.0),
('SUP-004', '江苏化工有限公司', '张总', '0511-88880004', 'zhang@jschemical.com', '南京市化工园', '化工原料', 'B', 88.0, 94.0);

-- ============================================
-- 6. 仓库表 (warehouse)
-- ============================================
CREATE TABLE IF NOT EXISTS warehouse (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '仓库ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '仓库编码',
    name VARCHAR(128) NOT NULL COMMENT '仓库名称',
    address VARCHAR(256) COMMENT '仓库地址',
    manager_id BIGINT COMMENT '管理员ID',
    capacity DECIMAL(12,2) COMMENT '容量(立方米)',
    description VARCHAR(512) COMMENT '备注',
    status VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-正常，INACTIVE-停用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- 插入测试仓库
INSERT INTO warehouse (code, name, address, capacity) VALUES
('WH-001', '原材料仓库A区', '1号库A区', 5000),
('WH-002', '零部件仓库B区', '2号库B区', 3000),
('WH-003', '包装材料仓库C区', '3号库C区', 2000);

-- ============================================
-- 7. 采购需求表 (requirement)
-- ============================================
CREATE TABLE IF NOT EXISTS requirement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '需求ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '需求单号',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    quantity DECIMAL(12,2) NOT NULL COMMENT '需求数量',
    unit_price DECIMAL(12,2) COMMENT '期望单价',
    total_amount DECIMAL(12,2) COMMENT '总金额',
    purpose VARCHAR(256) COMMENT '采购用途',
    expected_date DATE COMMENT '期望日期',
    dept_name VARCHAR(128) COMMENT '需求部门',
    requester_id BIGINT COMMENT '申请人ID',
    check_mode VARCHAR(16) DEFAULT 'SYSTEM' COMMENT '检测模式：SYSTEM/AI',
    check_result JSON COMMENT 'AI检测结果',
    status VARCHAR(16) DEFAULT 'PENDING' COMMENT '状态：PENDING-待审批，APPROVED-已审批，PURCHASING-采购中，COMPLETED-已完成，REJECTED-已驳回，CANCELLED-已取消',
    approval_id BIGINT COMMENT '审批人ID',
    approval_time DATETIME COMMENT '审批时间',
    approval_comment VARCHAR(256) COMMENT '审批意见',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (material_id) REFERENCES material(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购需求表';

-- 插入测试需求数据
INSERT INTO requirement (code, material_id, quantity, unit_price, total_amount, purpose, expected_date, dept_name, requester_id, status) VALUES
('PR-20260419-001', 1, 100, 8.50, 850, '设备维修备件', '2026-04-25', '生产部', 7, 'PENDING'),
('PR-20260419-002', 2, 200, 0.85, 170, '生产线螺栓更换', '2026-04-22', '生产部', 7, 'APPROVED'),
('PR-20260419-003', 3, 500, 4.20, 2100, '新产品试制材料', '2026-04-28', '研发部', 7, 'PENDING');

-- ============================================
-- 8. 采购订单表 (purchase_order)
-- ============================================
CREATE TABLE IF NOT EXISTS purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '订单编号',
    supplier_id BIGINT NOT NULL COMMENT '供应商ID',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '订单总金额',
    status VARCHAR(16) DEFAULT 'DRAFT' COMMENT '状态：DRAFT-草稿，CONFIRMED-已确认，PARTIAL_SHIPPED-部分发货，SHIPPED-已发货，RECEIVED-已收货，COMPLETED-已完成，CANCELLED-已取消',
    expected_date DATE COMMENT '期望交货日期',
    actual_date DATE COMMENT '实际交货日期',
    contact_name VARCHAR(64) COMMENT '联系人',
    contact_phone VARCHAR(32) COMMENT '联系电话',
    delivery_address VARCHAR(512) COMMENT '交货地址',
    payment_terms VARCHAR(128) COMMENT '付款条款',
    remark VARCHAR(512) COMMENT '备注',
    create_user_id BIGINT COMMENT '创建人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- 插入测试订单数据
INSERT INTO purchase_order (code, supplier_id, total_amount, status, expected_date, create_user_id) VALUES
('PO-20260419-001', 1, 850, 'CONFIRMED', '2026-04-25', 3),
('PO-20260419-002', 2, 170, 'SHIPPED', '2026-04-22', 3);

-- ============================================
-- 9. 采购订单明细表 (purchase_order_item)
-- ============================================
CREATE TABLE IF NOT EXISTS purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    quantity DECIMAL(12,2) NOT NULL COMMENT '订购数量',
    unit_price DECIMAL(12,2) COMMENT '单价',
    total_price DECIMAL(12,2) COMMENT '小计金额',
    shipped_qty DECIMAL(12,2) DEFAULT 0 COMMENT '已发货数量',
    received_qty DECIMAL(12,2) DEFAULT 0 COMMENT '已收货数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES purchase_order(id),
    FOREIGN KEY (material_id) REFERENCES material(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';

-- 插入测试订单明细
INSERT INTO purchase_order_item (order_id, material_id, quantity, unit_price, total_price) VALUES
(1, 1, 100, 8.50, 850),
(2, 2, 200, 0.85, 170);

-- ============================================
-- 10. 库存流水表 (inventory_log)
-- ============================================
CREATE TABLE IF NOT EXISTS inventory_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
    material_id BIGINT NOT NULL COMMENT '物资ID',
    warehouse_id BIGINT COMMENT '仓库ID',
    type VARCHAR(16) NOT NULL COMMENT '类型：IN-入库，OUT-出库，ADJUST-调整',
    quantity DECIMAL(12,2) NOT NULL COMMENT '变动数量',
    before_stock DECIMAL(12,2) COMMENT '变动前库存',
    after_stock DECIMAL(12,2) COMMENT '变动后库存',
    order_code VARCHAR(64) COMMENT '关联单号',
    reason VARCHAR(256) COMMENT '变动原因',
    operator_id BIGINT COMMENT '操作人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (material_id) REFERENCES material(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

-- 插入测试库存流水
INSERT INTO inventory_log (material_id, warehouse_id, type, quantity, before_stock, after_stock, reason, operator_id) VALUES
(1, 2, 'IN', 100, 0, 100, '初始入库', 4),
(1, 2, 'OUT', 20, 100, 80, '生产领用', 4),
(2, 2, 'IN', 800, 0, 800, '初始入库', 4);

-- ============================================
-- 11. 质检记录表 (qc_record)
-- ============================================
CREATE TABLE IF NOT EXISTS qc_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '质检ID',
    code VARCHAR(64) NOT NULL UNIQUE COMMENT '质检单号',
    material_id BIGINT COMMENT '物资ID',
    order_id BIGINT COMMENT '订单ID',
    batch_no VARCHAR(64) COMMENT '批次号',
    quantity DECIMAL(12,2) COMMENT '到货数量',
    sample_qty DECIMAL(12,2) COMMENT '抽样数量',
    result VARCHAR(16) COMMENT '结果：PASS-合格，FAIL-不合格',
    qualified_qty DECIMAL(12,2) COMMENT '合格数量',
    unqualified_qty DECIMAL(12,2) COMMENT '不合格数量',
    unqualified_reason VARCHAR(512) COMMENT '不合格原因',
    inspector_id BIGINT COMMENT '质检员ID',
    inspect_time DATETIME COMMENT '质检时间',
    remark VARCHAR(512) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (material_id) REFERENCES material(id),
    FOREIGN KEY (order_id) REFERENCES purchase_order(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检记录表';

-- ============================================
-- 12. 消息通知配置表 (notification_config)
-- ============================================
CREATE TABLE IF NOT EXISTS notification_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    type VARCHAR(32) NOT NULL COMMENT '类型：FEISHU-飞书',
    enabled TINYINT(1) DEFAULT 0 COMMENT '是否启用',
    webhook_url VARCHAR(512) COMMENT 'Webhook地址',
    config JSON COMMENT '其他配置',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知配置表';

-- 插入飞书配置（默认禁用）
INSERT INTO notification_config (type, enabled, webhook_url) VALUES
('FEISHU', 0, 'https://open.feishu.cn/open-apis/bot/v2/hook/your-webhook-id');

-- ============================================
-- 13. AI配置表 (ai_config)
-- ============================================
CREATE TABLE IF NOT EXISTS ai_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    provider VARCHAR(32) DEFAULT 'MINIMAX' COMMENT '提供商：MINIMAX/OPENAI/CUSTOM',
    api_key VARCHAR(256) COMMENT 'API密钥',
    api_endpoint VARCHAR(256) COMMENT 'API地址',
    model VARCHAR(64) COMMENT '模型名称',
    enabled TINYINT(1) DEFAULT 0 COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI配置表';

-- 插入默认AI配置（禁用状态）
INSERT INTO ai_config (provider, enabled) VALUES ('MINIMAX', 0);

-- ============================================
-- 14. 系统配置表 (system_config)
-- ============================================
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    config_key VARCHAR(64) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(256) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 插入默认配置
INSERT INTO system_config (config_key, config_value, description) VALUES
('approval.level1_threshold', '10000', '一级审批金额阈值'),
('approval.level2_threshold', '100000', '二级审批金额阈值'),
('alert.check_interval', '3600', '预警检测间隔(秒)'),
('alert.storm_protection', '14400', '预警风暴保护(秒)'),
('duplicate_check.enabled', 'true', '是否启用重复检测'),
('duplicate_check.default_mode', 'SYSTEM', '默认检测模式：SYSTEM/AI');

-- ============================================
-- 创建索引
-- ============================================
CREATE INDEX idx_material_code ON material(code);
CREATE INDEX idx_material_category ON material(category_id);
CREATE INDEX idx_requirement_code ON requirement(code);
CREATE INDEX idx_requirement_status ON requirement(status);
CREATE INDEX idx_requirement_material ON requirement(material_id);
CREATE INDEX idx_purchase_order_code ON purchase_order(code);
CREATE INDEX idx_purchase_order_status ON purchase_order(status);
CREATE INDEX idx_inventory_log_material ON inventory_log(material_id);
CREATE INDEX idx_inventory_log_type ON inventory_log(type);
CREATE INDEX idx_qc_record_code ON qc_record(code);

-- 完成
SELECT 'EIMS 数据库初始化完成!' AS message;

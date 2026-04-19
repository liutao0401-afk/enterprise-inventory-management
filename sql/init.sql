-- ===============================================
-- EIMS 企业物资管理系统 - 数据库初始化脚本
-- 版本: v1.0.0
-- 日期: 2026-04-18
-- ===============================================

-- ----------------------------
-- 1. 物资分类表
-- ----------------------------
CREATE TABLE IF NOT EXISTS m_material_category (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    parent_id       BIGINT UNSIGNED DEFAULT 0 COMMENT '父分类ID，0为顶级',
    category_code   VARCHAR(64) NOT NULL COMMENT '分类编码',
    category_name   VARCHAR(128) NOT NULL COMMENT '分类名称',
    sort_order      INT UNSIGNED DEFAULT 0 COMMENT '排序号',
    tenant_id       BIGINT UNSIGNED DEFAULT 1 COMMENT '租户ID',
    deleted         TINYINT UNSIGNED DEFAULT 0 COMMENT '删除标记',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_user     BIGINT UNSIGNED DEFAULT 0 COMMENT '创建人',
    update_user     BIGINT UNSIGNED DEFAULT 0 COMMENT '更新人',
    INDEX idx_parent (parent_id),
    INDEX idx_code (category_code),
    UNIQUE INDEX uk_code_tenant (category_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资分类表';

-- ----------------------------
-- 2. 物资主表
-- ----------------------------
CREATE TABLE IF NOT EXISTS m_material (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    material_code    VARCHAR(64) NOT NULL COMMENT '物资编码',
    material_name   VARCHAR(256) NOT NULL COMMENT '物资名称',
    category_id     BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
    unit            VARCHAR(16) NOT NULL COMMENT '计量单位',
    spec            VARCHAR(512) COMMENT '规格型号',
    safety_stock    DECIMAL(16,4) DEFAULT 0 COMMENT '安全库存',
    max_stock       DECIMAL(16,4) DEFAULT 0 COMMENT '最大库存',
    current_stock   DECIMAL(16,4) DEFAULT 0 COMMENT '当前库存',
    reorder_point   DECIMAL(16,4) DEFAULT 0 COMMENT '再订货点',
    lead_time_days  INT UNSIGNED DEFAULT 0 COMMENT '采购提前期（天）',
    unit_price      DECIMAL(16,4) DEFAULT 0 COMMENT '参考单价',
    location_code   VARCHAR(128) COMMENT '库位编码',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user     BIGINT UNSIGNED DEFAULT 0,
    update_user     BIGINT UNSIGNED DEFAULT 0,
    INDEX idx_code (material_code),
    INDEX idx_category (category_id),
    INDEX idx_name (material_name),
    UNIQUE INDEX uk_code_tenant (material_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物资主表';

-- ----------------------------
-- 3. 采购需求表
-- ----------------------------
CREATE TABLE IF NOT EXISTS p_purchase_requirement (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    requirement_code    VARCHAR(64) NOT NULL COMMENT '需求单号',
    department_id       BIGINT UNSIGNED COMMENT '需求部门ID',
    applicant_id        BIGINT UNSIGNED NOT NULL COMMENT '申请人ID',
    material_id         BIGINT UNSIGNED NOT NULL COMMENT '物资ID',
    quantity            DECIMAL(16,4) NOT NULL COMMENT '需求数量',
    unit_price          DECIMAL(16,4) COMMENT '期望单价',
    expected_date       DATE COMMENT '期望交货日期',
    priority            TINYINT UNSIGNED DEFAULT 5 COMMENT '优先级 1-10',
    purpose             VARCHAR(512) COMMENT '采购用途',
    status              VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    duplicate_check_result JSON COMMENT '重复检测结果',
    requirement_hash    VARCHAR(64) COMMENT '需求内容哈希',
    tenant_id           BIGINT UNSIGNED DEFAULT 1,
    deleted             TINYINT UNSIGNED DEFAULT 0,
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user         BIGINT UNSIGNED DEFAULT 0,
    update_user         BIGINT UNSIGNED DEFAULT 0,
    INDEX idx_code (requirement_code),
    INDEX idx_status (status),
    INDEX idx_material (material_id),
    INDEX idx_hash (requirement_hash),
    UNIQUE INDEX uk_code_tenant (requirement_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购需求表';

-- ----------------------------
-- 4. 供应商表
-- ----------------------------
CREATE TABLE IF NOT EXISTS m_supplier (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    supplier_code   VARCHAR(64) NOT NULL COMMENT '供应商编码',
    supplier_name   VARCHAR(256) NOT NULL COMMENT '供应商名称',
    contact_person  VARCHAR(64) COMMENT '联系人',
    contact_phone   VARCHAR(32) COMMENT '联系电话',
    address         VARCHAR(512) COMMENT '地址',
    credit_level    VARCHAR(16) COMMENT '信用等级',
    status          VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user     BIGINT UNSIGNED DEFAULT 0,
    update_user     BIGINT UNSIGNED DEFAULT 0,
    INDEX idx_code (supplier_code),
    INDEX idx_status (status),
    UNIQUE INDEX uk_code_tenant (supplier_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商表';

-- ----------------------------
-- 5. 采购订单表
-- ----------------------------
CREATE TABLE IF NOT EXISTS p_purchase_order (
    id                  BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_code          VARCHAR(64) NOT NULL COMMENT '订单编号',
    supplier_id         BIGINT UNSIGNED NOT NULL COMMENT '供应商ID',
    order_date          DATE NOT NULL COMMENT '下单日期',
    expected_date       DATE COMMENT '期望交货日期',
    total_amount        DECIMAL(16,4) DEFAULT 0 COMMENT '订单总金额',
    discount_rate       DECIMAL(5,4) DEFAULT 0 COMMENT '折扣率',
    net_amount          DECIMAL(16,4) DEFAULT 0 COMMENT '净金额',
    payment_status      VARCHAR(16) DEFAULT 'UNPAID' COMMENT '付款状态',
    delivery_status     VARCHAR(16) DEFAULT 'UNSHIPPED' COMMENT '发货状态',
    status              VARCHAR(16) NOT NULL DEFAULT 'DRAFT' COMMENT '订单状态',
    notes               TEXT COMMENT '备注',
    tenant_id           BIGINT UNSIGNED DEFAULT 1,
    deleted             TINYINT UNSIGNED DEFAULT 0,
    create_time         DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user         BIGINT UNSIGNED DEFAULT 0,
    update_user         BIGINT UNSIGNED DEFAULT 0,
    INDEX idx_code (order_code),
    INDEX idx_supplier (supplier_id),
    INDEX idx_status (status),
    UNIQUE INDEX uk_code_tenant (order_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';

-- ----------------------------
-- 6. 采购订单明细表
-- ----------------------------
CREATE TABLE IF NOT EXISTS p_purchase_order_item (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
    material_id     BIGINT UNSIGNED NOT NULL COMMENT '物资ID',
    quantity        DECIMAL(16,4) NOT NULL COMMENT '订购数量',
    unit_price      DECIMAL(16,4) NOT NULL COMMENT '单价',
    total_amount   DECIMAL(16,4) NOT NULL COMMENT '小计金额',
    delivered_qty   DECIMAL(16,4) DEFAULT 0 COMMENT '已发货数量',
    accepted_qty    DECIMAL(16,4) DEFAULT 0 COMMENT '已验收数量',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_order (order_id),
    INDEX idx_material (material_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单明细表';

-- ----------------------------
-- 7. 仓库表
-- ----------------------------
CREATE TABLE IF NOT EXISTS w_warehouse (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    warehouse_code   VARCHAR(64) NOT NULL COMMENT '仓库编码',
    warehouse_name   VARCHAR(128) NOT NULL COMMENT '仓库名称',
    warehouse_type   VARCHAR(16) COMMENT '仓库类型',
    address         VARCHAR(512) COMMENT '仓库地址',
    manager_id      BIGINT UNSIGNED COMMENT '负责人ID',
    status          VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user     BIGINT UNSIGNED DEFAULT 0,
    update_user     BIGINT UNSIGNED DEFAULT 0,
    INDEX idx_code (warehouse_code),
    INDEX idx_status (status),
    UNIQUE INDEX uk_code_tenant (warehouse_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库表';

-- ----------------------------
-- 8. 库位表
-- ----------------------------
CREATE TABLE IF NOT EXISTS w_location (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    warehouse_id    BIGINT UNSIGNED NOT NULL COMMENT '仓库ID',
    location_code   VARCHAR(64) NOT NULL COMMENT '库位编码',
    location_name   VARCHAR(128) COMMENT '库位名称',
    location_type   VARCHAR(16) COMMENT '库位类型',
    capacity        DECIMAL(16,4) DEFAULT 0 COMMENT '容量',
    status          VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_warehouse (warehouse_id),
    INDEX idx_code (location_code),
    UNIQUE INDEX uk_code_warehouse (warehouse_id, location_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库位表';

-- ----------------------------
-- 9. 库存流水表
-- ----------------------------
CREATE TABLE IF NOT EXISTS w_inventory_transaction (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    trans_code      VARCHAR(64) NOT NULL COMMENT '流水编号',
    material_id     BIGINT UNSIGNED NOT NULL COMMENT '物资ID',
    warehouse_id    BIGINT UNSIGNED NOT NULL COMMENT '仓库ID',
    location_id     BIGINT UNSIGNED COMMENT '库位ID',
    trans_type      VARCHAR(16) NOT NULL COMMENT '类型：INBOUND/OUTBOUND/TRANSFER/ADJUST',
    quantity_before DECIMAL(16,4) NOT NULL COMMENT '变动前数量',
    quantity_change DECIMAL(16,4) NOT NULL COMMENT '变动数量',
    quantity_after  DECIMAL(16,4) NOT NULL COMMENT '变动后数量',
    reference_type  VARCHAR(32) COMMENT '关联单据类型',
    reference_id    BIGINT UNSIGNED COMMENT '关联单据ID',
    batch_no        VARCHAR(64) COMMENT '批次号',
    expiry_date    DATE COMMENT '有效期至',
    operator_id    BIGINT UNSIGNED NOT NULL COMMENT '操作人',
    operate_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    tenant_id      BIGINT UNSIGNED DEFAULT 1,
    deleted        TINYINT UNSIGNED DEFAULT 0,
    create_time    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_material (material_id),
    INDEX idx_trans_type (trans_type),
    INDEX idx_operate_time (operate_time),
    INDEX idx_batch (batch_no),
    INDEX idx_reference (reference_type, reference_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存流水表';

-- ----------------------------
-- 10. 质检记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS q_quality_record (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    record_code     VARCHAR(64) NOT NULL COMMENT '质检单号',
    order_id        BIGINT UNSIGNED NOT NULL COMMENT '关联采购订单ID',
    material_id     BIGINT UNSIGNED NOT NULL COMMENT '物资ID',
    batch_no        VARCHAR(64) COMMENT '来料批次号',
    sample_size     INT UNSIGNED COMMENT '抽样数量',
    quantity        DECIMAL(16,4) NOT NULL COMMENT '来料数量',
    inspect_date    DATE NOT NULL COMMENT '检验日期',
    inspector_id    BIGINT UNSIGNED NOT NULL COMMENT '质检员ID',
    result          VARCHAR(16) NOT NULL COMMENT '结果：PASS/FAIL/PENDING',
    defect_items    JSON COMMENT '不合格项详情',
    report_file     VARCHAR(512) COMMENT '质检报告路径',
    notes           TEXT COMMENT '备注',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user     BIGINT UNSIGNED DEFAULT 0,
    update_user     BIGINT UNSIGNED DEFAULT 0,
    INDEX idx_code (record_code),
    INDEX idx_order (order_id),
    INDEX idx_result (result),
    INDEX idx_inspect_date (inspect_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='质检记录表';

-- ----------------------------
-- 11. 预警配置表
-- ----------------------------
CREATE TABLE IF NOT EXISTS a_alert_config (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    material_id     BIGINT UNSIGNED NOT NULL COMMENT '物资ID（0表示所有）',
    warehouse_id    BIGINT UNSIGNED DEFAULT 0 COMMENT '仓库ID（0表示所有）',
    alert_type      VARCHAR(32) NOT NULL COMMENT '预警类型',
    threshold_min   DECIMAL(16,4) COMMENT '阈值下限',
    threshold_max   DECIMAL(16,4) COMMENT '阈值上限',
    notify_channels JSON NOT NULL COMMENT '通知渠道',
    notify_users    JSON COMMENT '通知用户列表',
    webhook_url     VARCHAR(512) COMMENT 'Webhook URL',
    enabled         TINYINT UNSIGNED DEFAULT 1 COMMENT '是否启用',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    create_user     BIGINT UNSIGNED DEFAULT 0,
    update_user     BIGINT UNSIGNED DEFAULT 0,
    INDEX idx_material (material_id),
    INDEX idx_type (alert_type),
    INDEX idx_enabled (enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警配置表';

-- ----------------------------
-- 12. 预警记录表
-- ----------------------------
CREATE TABLE IF NOT EXISTS a_alert_record (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    alert_code      VARCHAR(64) NOT NULL COMMENT '预警编号',
    config_id       BIGINT UNSIGNED NOT NULL COMMENT '预警配置ID',
    material_id     BIGINT UNSIGNED NOT NULL COMMENT '物资ID',
    warehouse_id    BIGINT UNSIGNED COMMENT '仓库ID',
    alert_type      VARCHAR(32) NOT NULL COMMENT '预警类型',
    alert_level     VARCHAR(16) NOT NULL COMMENT '级别：INFO/WARNING/CRITICAL',
    title           VARCHAR(256) NOT NULL COMMENT '预警标题',
    message         TEXT NOT NULL COMMENT '预警详情',
    current_value   DECIMAL(16,4) COMMENT '触发时当前值',
    threshold_value DECIMAL(16,4) COMMENT '触发阈值',
    notify_status   VARCHAR(16) DEFAULT 'PENDING' COMMENT '通知状态',
    notified_time   DATETIME COMMENT '通知发送时间',
    acknowledged    TINYINT UNSIGNED DEFAULT 0 COMMENT '是否已确认',
    acknowledged_by BIGINT UNSIGNED COMMENT '确认人',
    acknowledged_at DATETIME COMMENT '确认时间',
    resolved        TINYINT UNSIGNED DEFAULT 0 COMMENT '是否已解决',
    resolved_by     BIGINT UNSIGNED COMMENT '解决人',
    resolved_at     DATETIME COMMENT '解决时间',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_code (alert_code),
    INDEX idx_config (config_id),
    INDEX idx_material (material_id),
    INDEX idx_type (alert_type),
    INDEX idx_status (notify_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预警记录表';

-- ----------------------------
-- 13. 系统用户表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(64) NOT NULL COMMENT '用户名',
    password        VARCHAR(256) NOT NULL COMMENT '密码（加密）',
    real_name       VARCHAR(64) COMMENT '真实姓名',
    phone           VARCHAR(32) COMMENT '手机号',
    email           VARCHAR(128) COMMENT '邮箱',
    dept_id         BIGINT UNSIGNED COMMENT '部门ID',
    post            VARCHAR(64) COMMENT '岗位',
    status          VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_dept (dept_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- ----------------------------
-- 14. 系统角色表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_code       VARCHAR(64) NOT NULL COMMENT '角色编码',
    role_name       VARCHAR(128) NOT NULL COMMENT '角色名称',
    role_type       VARCHAR(16) COMMENT '角色类型',
    data_scope      VARCHAR(16) DEFAULT 'DEPT' COMMENT '数据范围',
    status          VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_code (role_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色表';

-- ----------------------------
-- 15. 用户角色关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_user_role (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    role_id         BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_role (role_id),
    UNIQUE INDEX uk_user_role (user_id, role_id, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- 16. 权限表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_permission (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    parent_id       BIGINT UNSIGNED DEFAULT 0 COMMENT '父权限ID',
    permission_code VARCHAR(64) NOT NULL COMMENT '权限编码',
    permission_name VARCHAR(128) NOT NULL COMMENT '权限名称',
    permission_type VARCHAR(16) NOT NULL COMMENT '类型：MENU/BUTTON/API',
    menu_path       VARCHAR(256) COMMENT '菜单路径',
    menu_icon       VARCHAR(64) COMMENT '菜单图标',
    sort_order      INT UNSIGNED DEFAULT 0 COMMENT '排序',
    status          VARCHAR(16) DEFAULT 'ACTIVE' COMMENT '状态',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent (parent_id),
    INDEX idx_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- ----------------------------
-- 17. 角色权限关联表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    role_id         BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    permission_id   BIGINT UNSIGNED NOT NULL COMMENT '权限ID',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_role (role_id),
    INDEX idx_permission (permission_id),
    UNIQUE INDEX uk_role_permission (role_id, permission_id, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- ----------------------------
-- 18. 部门表
-- ----------------------------
CREATE TABLE IF NOT EXISTS sys_department (
    id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    parent_id       BIGINT UNSIGNED DEFAULT 0 COMMENT '父部门ID',
    dept_code       VARCHAR(64) NOT NULL COMMENT '部门编码',
    dept_name       VARCHAR(128) NOT NULL COMMENT '部门名称',
    sort_order      INT UNSIGNED DEFAULT 0 COMMENT '排序',
    tenant_id       BIGINT UNSIGNED DEFAULT 1,
    deleted         TINYINT UNSIGNED DEFAULT 0,
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_parent (parent_id),
    INDEX idx_code (dept_code),
    UNIQUE INDEX uk_code_tenant (dept_code, tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 初始化数据
-- ----------------------------
-- 租户管理员
INSERT INTO sys_user (id, username, password, real_name, tenant_id, status) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE3jLGD6UTJmKTiHK', '系统管理员', 1, 'ACTIVE');

-- 默认角色
INSERT INTO sys_role (id, role_code, role_name, role_type, data_scope, tenant_id, status) VALUES
(1, 'SUPER_ADMIN', '超级管理员', 'SYSTEM', 'ALL', 1, 'ACTIVE'),
(2, 'PURCHASE_MGR', '采购主管', 'BUSINESS', 'DEPT', 1, 'ACTIVE'),
(3, 'PURCHASER', '采购员', 'BUSINESS', 'DEPT', 1, 'ACTIVE'),
(4, 'WAREHOUSE_MGR', '仓库管理员', 'BUSINESS', 'DEPT', 1, 'ACTIVE'),
(5, 'QC_INSPECTOR', '质检员', 'BUSINESS', 'DEPT', 1, 'ACTIVE'),
(6, 'FINANCE', '财务会计', 'BUSINESS', 'DEPT', 1, 'ACTIVE'),
(7, 'DEPT_USER', '需求部门用户', 'BUSINESS', 'DEPT', 1, 'ACTIVE'),
(8, 'DATA_ANALYST', '数据分析师', 'BUSINESS', 'DEPT', 1, 'ACTIVE');

-- 部门
INSERT INTO sys_department (id, dept_code, dept_name, parent_id, tenant_id) VALUES
(1, 'D001', '综合管理部', 0, 1),
(2, 'D002', '采购部', 1, 1),
(3, 'D003', '仓储部', 1, 1),
(4, 'D004', '质检部', 1, 1),
(5, 'D005', '财务部', 1, 1),
(6, 'D006', '生产车间', 1, 1);

-- 仓库
INSERT INTO w_warehouse (id, warehouse_code, warehouse_name, warehouse_type, tenant_id, status) VALUES
(1, 'WH01', '主仓库', 'MAIN', 1, 'ACTIVE'),
(2, 'WH02', '辅料仓库', 'SUB', 1, 'ACTIVE');

-- 物资分类
INSERT INTO m_material_category (id, parent_id, category_code, category_name, sort_order, tenant_id) VALUES
(1, 0, 'CAT_RAW', '原材料', 1, 1),
(2, 0, 'CAT_PART', '零部件', 2, 1),
(3, 0, 'CAT_PACK', '包装材料', 3, 1),
(4, 0, 'CAT_MRO', 'MRO备件', 4, 1),
(5, 0, 'CAT_CONSUM', '办公耗材', 5, 1);

-- 预警配置（默认模板）
INSERT INTO a_alert_config (id, material_id, warehouse_id, alert_type, threshold_min, threshold_max, notify_channels, enabled, tenant_id) VALUES
(1, 0, 0, 'LOW_STOCK', 0, NULL, '["SYSTEM"]', 1, 1),
(2, 0, 0, 'HIGH_STOCK', NULL, 0, '["SYSTEM"]', 1, 1),
(3, 0, 0, 'EXPIRE_SOON', 30, NULL, '["SYSTEM"]', 1, 1),
(4, 0, 0, 'PENDING_QC', 48, NULL, '["SYSTEM"]', 1, 1),
(5, 0, 0, 'DELIVERY_OVERDUE', 0, NULL, '["SYSTEM"]', 1, 1);

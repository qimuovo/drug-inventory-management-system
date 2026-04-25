-- 创建数据库
CREATE DATABASE IF NOT EXISTS drug_inventory CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE drug_inventory;

-- 用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_account VARCHAR(64)                        NOT NULL COMMENT '用户账号',
    user_name    VARCHAR(64) COMMENT '用户名',
    phone        VARCHAR(32) COMMENT '手机号码',
    avatar       VARCHAR(512) COMMENT '头像URL',
    password     VARCHAR(512) COMMENT '密码',
    is_deleted   TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT ='用户表';

-- 生产厂家表
DROP TABLE IF EXISTS t_manufacturer;
CREATE TABLE t_manufacturer
(
    id                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    manufacturer_name VARCHAR(100)                       NOT NULL COMMENT '厂家名称',
    contact_person    VARCHAR(64) COMMENT '联系人',
    phone             VARCHAR(32) COMMENT '联系电话',
    address           VARCHAR(255) COMMENT '地址',
    is_deleted        TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT ='生产厂家表';

-- 药品表
DROP TABLE IF EXISTS t_drug;
CREATE TABLE t_drug
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    drug_name       VARCHAR(100)                       NOT NULL COMMENT '药品名称',
    drug_code       VARCHAR(64)                        NOT NULL UNIQUE COMMENT '药品编号',
    specification   VARCHAR(100) COMMENT '规格',
    manufacturer_id BIGINT COMMENT '生产厂家ID',
    is_deleted      TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT ='药品表';

-- 入库单
DROP TABLE IF EXISTS t_inbound;
CREATE TABLE t_inbound
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    inbound_no   VARCHAR(64)                        NOT NULL UNIQUE COMMENT '入库单号',
    operator_id  BIGINT COMMENT '操作人',
    inbound_date DATETIME COMMENT '入库日期',
    remark       VARCHAR(255) COMMENT '备注',
    is_deleted   TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time  DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT ='入库单';

-- 入库明细
DROP TABLE IF EXISTS t_inbound_item;
CREATE TABLE t_inbound_item
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    inbound_id  BIGINT                             NOT NULL COMMENT '入库单ID',
    drug_id     BIGINT                             NOT NULL COMMENT '药品ID',
    batch_no    VARCHAR(64)                        NOT NULL COMMENT '批号',
    quantity    INT                                NOT NULL COMMENT '入库数量',
    price       DECIMAL(10, 2) COMMENT '入库价格',
    amount      DECIMAL(12, 2) COMMENT '金额',
    is_deleted  TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间'
) COMMENT ='入库明细';

-- 入库退货
DROP TABLE IF EXISTS t_inbound_return;
CREATE TABLE t_inbound_return
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    inbound_item_id BIGINT COMMENT '入库明细ID',
    return_quantity INT COMMENT '退货数量',
    return_price    DECIMAL(10, 2) COMMENT '退货价格',
    reason          VARCHAR(255) COMMENT '退货原因',
    operator_id     BIGINT COMMENT '操作人',
    return_date     DATETIME COMMENT '退货日期',
    is_deleted      TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time     DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间'
) COMMENT ='入库退货';

-- 出库单
DROP TABLE IF EXISTS t_outbound;
CREATE TABLE t_outbound
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    outbound_no   VARCHAR(64)                        NOT NULL UNIQUE COMMENT '出库单号',
    operator_id   BIGINT COMMENT '操作人',
    outbound_date DATETIME COMMENT '出库日期',
    remark        VARCHAR(255) COMMENT '备注',
    is_deleted    TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间',
    update_time   DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT ='出库单';

-- 出库明细
DROP TABLE IF EXISTS t_outbound_item;
CREATE TABLE t_outbound_item
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    outbound_id BIGINT                             NOT NULL COMMENT '出库单ID',
    drug_id     BIGINT                             NOT NULL COMMENT '药品ID',
    batch_no    VARCHAR(64)                        NOT NULL COMMENT '批号',
    quantity    INT                                NOT NULL COMMENT '出库数量',
    price       DECIMAL(10, 2) COMMENT '出库价格',
    amount      DECIMAL(12, 2) COMMENT '金额',
    is_deleted  TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间'
) COMMENT ='出库明细';

-- 出库退库
DROP TABLE IF EXISTS t_outbound_return;
CREATE TABLE t_outbound_return
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    outbound_item_id BIGINT COMMENT '出库明细ID',
    return_quantity  INT COMMENT '退库数量',
    return_price     DECIMAL(10, 2) COMMENT '退库价格',
    reason           VARCHAR(255) COMMENT '退库原因',
    operator_id      BIGINT COMMENT '操作人',
    return_date      DATETIME COMMENT '退库日期',
    is_deleted       TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_time      DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL COMMENT '创建时间'
) COMMENT ='出库退库';

-- 库存表
DROP TABLE IF EXISTS t_inventory;
CREATE TABLE t_inventory
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    drug_id     BIGINT                             NOT NULL COMMENT '药品ID',
    batch_no    VARCHAR(64)                        NOT NULL COMMENT '批号',
    quantity    INT      DEFAULT 0 COMMENT '库存数量',
    is_deleted  TINYINT  DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_drug_batch (drug_id, batch_no)
) COMMENT ='库存表';
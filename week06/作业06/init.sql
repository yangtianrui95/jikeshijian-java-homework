CREATE
DATABASE IF NOT EXISTS `commerce`;

use `commerce`;

-- 用户表
CREATE TABLE IF NOT EXISTS `t_users`
(
    `id`          INT(11)        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`        VARCHAR(16)    NOT NULL COMMENT '用户名',
    `password`    VARCHAR(16)    NOT NULL COMMENT '密码',
    `phoneNumber` CHAR(11)       NOT NULL COMMENT '手机号',
    `idcard`      CHAR(16)       NOT NULL COMMENT '身份证号',
    `money`       DECIMAL(10, 2) NOT NULL COMMENT '余额',
    `create_time` DATETIME       NOT NULL COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL COMMENT '更新时间',
    primary key (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 商品表
CREATE TABLE IF NOT EXISTS `t_commodity`
(
    `id`          INT(11)        NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`        VARCHAR(16)    NOT NULL COMMENT '商品名',
    `desc`        VARCHAR(1024)  NOT NULL COMMENT '商品描述',
    `price`       DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `shop_id`     INT(11)        NOT NULL COMMENT '店铺id',
    'remain_size' INT(11)        NOT NULL COMMENT '库存',
    `create_time` DATETIME       NOT NULL COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL COMMENT '更新时间',
    primary key (`id`),
    index         `idx_shop_id` (`shop_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- 商家
CREATE TABLE IF NOT EXISTS `t_shop`
(
    `id`           INT(11)       NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`         VARCHAR(16)   NOT NULL COMMENT '商家名',
    `desc`         VARCHAR(1024) NOT NULL COMMENT '商家描述',
    `commodity_id` INT(11)       NOT NULL COMMENT '商品id',
    `create_time`  DATETIME      NOT NULL COMMENT '创建时间',
    `update_time`  DATETIME      NOT NULL COMMENT '更新时间',
    primary key (`id`),
    index          `idx_commodity_id` (`commodity_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

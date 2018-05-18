/*
Navicat MySQL Data Transfer

Source Server         : mine阿里云
Source Server Version : 50718
Source Host           : 123.56.228.68:3306
Source Database       : erlangshen

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-05-18 18:10:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for client
-- ----------------------------
DROP TABLE IF EXISTS `client`;
CREATE TABLE `client` (
  `id` varchar(64) NOT NULL COMMENT 'client key',
  `name` varchar(64) NOT NULL COMMENT 'client名称',
  `created_by` varchar(64) NOT NULL COMMENT '创建人',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除 0未删除 1删除',
  `deleted_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_client_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='客户端表';

-- ----------------------------
-- Table structure for client_mail
-- ----------------------------
DROP TABLE IF EXISTS `client_mail`;
CREATE TABLE `client_mail` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'client key',
  `client_id` varchar(64) NOT NULL COMMENT 'client名称',
  `mail` varchar(64) DEFAULT NULL COMMENT '邮件',
  `username` varchar(64) DEFAULT NULL,
  `pwd` varchar(64) DEFAULT NULL COMMENT '用户注册发送邮件密码',
  `smtp` varchar(64) DEFAULT NULL,
  `type` varchar(10) DEFAULT NULL COMMENT '注册邮件内容',
  `subject` varchar(10) DEFAULT NULL COMMENT '标题',
  `text` varchar(21000) DEFAULT NULL COMMENT '注册短信内容',
  PRIMARY KEY (`id`),
  KEY `idx_client_name` (`client_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='客户端表';

-- ----------------------------
-- Table structure for client_phone
-- ----------------------------
DROP TABLE IF EXISTS `client_phone`;
CREATE TABLE `client_phone` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'client key',
  `client_id` varchar(64) NOT NULL COMMENT 'client名称',
  `platform` varchar(64) DEFAULT NULL COMMENT '短信平台',
  `ak` varchar(64) DEFAULT NULL,
  `sk` varchar(64) DEFAULT NULL COMMENT '用户注册发送邮件密码',
  `sign` varchar(64) DEFAULT NULL COMMENT '签名',
  `tmplate` varchar(64) DEFAULT NULL COMMENT '模板',
  `type` varchar(10) DEFAULT NULL COMMENT '类型',
  `text` varchar(70) DEFAULT NULL COMMENT '短信内容',
  PRIMARY KEY (`id`),
  KEY `idx_client_name` (`client_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='客户端表';

-- ----------------------------
-- Table structure for client_security
-- ----------------------------
DROP TABLE IF EXISTS `client_security`;
CREATE TABLE `client_security` (
  `client_id` varchar(64) NOT NULL,
  `is_check_place` tinyint(1) DEFAULT '0' COMMENT '是否异地登陆检查 0不检查 1检查',
  `check_place_priority` int(1) DEFAULT NULL COMMENT '通知优先级 0都通知 1手机优先 2邮件优先',
  `check_place_phone_type_id` int(11) DEFAULT NULL COMMENT '异地登陆邮件通知类型',
  `check_place_mail_type_id` int(11) DEFAULT NULL COMMENT '异地登陆手机通知类型',
  `is_check_platform` int(1) DEFAULT '1' COMMENT '是否对登陆平台检查 0多平台多账号可同时登陆 1可以多平台登录，同一平台只能1个账号在线 2所有平台只能1个账号在线',
  `check_platform_type` int(1) DEFAULT '0' COMMENT '登录冲突操作 0登出之前登陆的账号 1新登陆请求失败',
  `login_api` varchar(255) DEFAULT NULL COMMENT '登录通知接口',
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for code
-- ----------------------------
DROP TABLE IF EXISTS `code`;
CREATE TABLE `code` (
  `id` varchar(64) NOT NULL,
  `parent_id` varchar(64) DEFAULT NULL,
  `group_id` varchar(64) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  `sqeuence` int(11) DEFAULT NULL COMMENT '???',
  `enable` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for key
-- ----------------------------
DROP TABLE IF EXISTS `key`;
CREATE TABLE `key` (
  `access` varbinary(255) NOT NULL,
  `secret` varbinary(255) NOT NULL,
  `client_id` varchar(64) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '1正常',
  `created_time` datetime NOT NULL,
  PRIMARY KEY (`access`,`secret`),
  KEY `idx_key_ak_sk` (`access`,`secret`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) NOT NULL,
  `ip` varchar(128) DEFAULT NULL COMMENT '登录ip',
  `login_time` datetime NOT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for permission_menu
-- ----------------------------
DROP TABLE IF EXISTS `permission_menu`;
CREATE TABLE `permission_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` varchar(64) NOT NULL,
  `parent_id` int(11) DEFAULT '0',
  `type` int(1) DEFAULT NULL COMMENT '0菜单 1功能',
  `name` varchar(255) DEFAULT NULL COMMENT '菜单名称',
  `url` varchar(255) DEFAULT NULL COMMENT '菜单url',
  `tag` varchar(255) DEFAULT NULL COMMENT '标签',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8 COMMENT='菜单表';

-- ----------------------------
-- Table structure for permission_role
-- ----------------------------
DROP TABLE IF EXISTS `permission_role`;
CREATE TABLE `permission_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `client_id` varchar(64) NOT NULL,
  `role` varchar(255) NOT NULL COMMENT '角色',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Table structure for permission_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `permission_role_menu`;
CREATE TABLE `permission_role_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8 COMMENT='角色功能表';

-- ----------------------------
-- Table structure for permission_user_role
-- ----------------------------
DROP TABLE IF EXISTS `permission_user_role`;
CREATE TABLE `permission_user_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` varchar(64) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8 COMMENT='用户角色表';

-- ----------------------------
-- Table structure for token
-- ----------------------------
DROP TABLE IF EXISTS `token`;
CREATE TABLE `token` (
  `id` varbinary(64) NOT NULL,
  `user_id` varchar(64) NOT NULL,
  `created_time` datetime DEFAULT NULL,
  `active_time` datetime NOT NULL COMMENT '有效时间',
  `ip` varchar(128) DEFAULT NULL COMMENT '客户端ip',
  `platform` varchar(255) DEFAULT NULL COMMENT '登录平台 用于区分在不同平台是否可以重复登录(win,mac,web,android,ios)',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` varchar(64) NOT NULL,
  `client_id` varchar(64) DEFAULT NULL COMMENT '客户端id',
  `pwd` varchar(255) NOT NULL COMMENT '密码',
  `username` varchar(32) DEFAULT NULL COMMENT '用户名',
  `mail` varchar(32) DEFAULT NULL COMMENT '邮箱',
  `mail_verify` tinyint(1) DEFAULT '0' COMMENT '邮箱验证 0未验证 1验证',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号码',
  `phone_verify` tinyint(1) DEFAULT '0' COMMENT '手机号码验证 0未验证 1验证',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态',
  PRIMARY KEY (`id`),
  KEY `idx_user_client_group_name` (`client_id`,`username`),
  KEY `idx_user_client_group_nikname` (`client_id`),
  KEY `idx_user_client_group_mail` (`client_id`,`mail`),
  KEY `idx_user_client_group_phone` (`client_id`,`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` varchar(64) NOT NULL,
  `source` varchar(255) DEFAULT NULL COMMENT '来源',
  `nickname` varchar(8) DEFAULT NULL COMMENT '昵称',
  `tel` varchar(32) DEFAULT NULL COMMENT '电话',
  `qq` varchar(32) DEFAULT NULL COMMENT 'QQ',
  `weixin` varchar(32) DEFAULT NULL COMMENT '微信',
  `weibo` varchar(32) DEFAULT NULL COMMENT '新浪微博',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0女 1男',
  `idcard` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `certification` int(1) DEFAULT '0' COMMENT '实名认证 0未实名 1认证中 2认证失败 3认证成功',
  `certification_fail_msg` varchar(255) DEFAULT NULL COMMENT '实名认证失败原因',
  `province` varchar(64) DEFAULT NULL COMMENT '省',
  `city` varchar(64) DEFAULT NULL COMMENT '市',
  `area` varchar(64) DEFAULT NULL COMMENT '区',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_client_group_nikname` (`nickname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Table structure for user_recycle
-- ----------------------------
DROP TABLE IF EXISTS `user_recycle`;
CREATE TABLE `user_recycle` (
  `id` varchar(64) NOT NULL,
  `client_id` varchar(64) DEFAULT NULL COMMENT '客户端',
  `source` varchar(255) DEFAULT NULL COMMENT '来源',
  `pwd` varchar(255) DEFAULT NULL COMMENT '密码',
  `username` varchar(32) DEFAULT NULL COMMENT '用户名',
  `nickname` varchar(8) DEFAULT NULL COMMENT '昵称',
  `mail` varchar(32) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(32) DEFAULT NULL COMMENT '手机号码',
  `tel` varchar(32) DEFAULT NULL COMMENT '电话',
  `qq` varchar(32) DEFAULT NULL COMMENT 'QQ',
  `weixin` varchar(32) DEFAULT NULL COMMENT '微信',
  `wexin_img` varchar(255) DEFAULT NULL COMMENT '微信二维码',
  `weibo` varchar(32) DEFAULT NULL COMMENT '新浪微博',
  `head` varchar(255) DEFAULT NULL COMMENT '头像',
  `name` varchar(32) DEFAULT NULL COMMENT '姓名',
  `sex` tinyint(1) DEFAULT NULL COMMENT '性别 0女 1男',
  `idcard` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `certification` int(1) DEFAULT '0' COMMENT '实名认证 0未实名 1认证中 2认证失败 3认证成功',
  `province` varchar(64) DEFAULT NULL COMMENT '省',
  `city` varchar(64) DEFAULT NULL COMMENT '市',
  `area` varchar(64) DEFAULT NULL COMMENT '区',
  `address` varchar(255) DEFAULT NULL COMMENT '地址',
  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
  `operator_time` datetime NOT NULL COMMENT '操作时间',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态 1删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_client_group_name` (`client_id`,`username`),
  KEY `idx_user_client_group_nikname` (`client_id`,`nickname`),
  KEY `idx_user_client_group_mail` (`client_id`,`mail`),
  KEY `idx_user_client_group_phone` (`client_id`,`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户表';

-- ----------------------------
-- Table structure for validate
-- ----------------------------
DROP TABLE IF EXISTS `validate`;
CREATE TABLE `validate` (
  `user_id` varchar(64) NOT NULL,
  `type` varchar(10) NOT NULL COMMENT '类型 mail phone',
  `code` varchar(255) NOT NULL,
  `created_time` datetime NOT NULL,
  PRIMARY KEY (`user_id`,`type`),
  KEY `idx_validate_user_id_type` (`user_id`,`type`),
  KEY `idx_validate_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='注册验证码';

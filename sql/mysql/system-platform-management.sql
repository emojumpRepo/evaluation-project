-- 平台管理相关表结构

-- 轮播图表
CREATE TABLE `system_carousel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '轮播图ID',
  `title` varchar(100) NOT NULL COMMENT '轮播图标题',
  `subtitle` varchar(200) DEFAULT NULL COMMENT '副标题',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `link_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0=禁用 1=启用）',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '类型（1=跳转链接 2=弹窗）',
  `popup_content` text COMMENT '弹窗内容',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_sort` (`sort`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- 政策配置表
CREATE TABLE `system_policy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '政策ID',
  `type` varchar(50) NOT NULL COMMENT '政策类型（service_agreement=服务协议，privacy_policy=隐私协议）',
  `title` varchar(100) NOT NULL COMMENT '政策标题',
  `content` longtext NOT NULL COMMENT '政策内容（富文本）',
  `version` varchar(20) NOT NULL DEFAULT '1.0' COMMENT '版本号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0=禁用 1=启用）',
  `effective_time` datetime DEFAULT NULL COMMENT '生效时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_tenant` (`type`, `tenant_id`, `deleted`),
  KEY `idx_status` (`status`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='政策配置表';

-- 初始化政策数据
INSERT INTO `system_policy` (`type`, `title`, `content`, `version`, `status`, `effective_time`, `creator`, `updater`, `tenant_id`) VALUES
('service_agreement', '服务协议', '<h1>服务协议</h1><p>请在此处填写服务协议内容...</p>', '1.0', 1, NOW(), 'admin', 'admin', 1),
('privacy_policy', '隐私政策', '<h1>隐私政策</h1><p>请在此处填写隐私政策内容...</p>', '1.0', 1, NOW(), 'admin', 'admin', 1);

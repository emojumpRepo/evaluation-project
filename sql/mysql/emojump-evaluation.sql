-- ----------------------------
-- 测评模块相关表
-- ----------------------------

-- 问卷表
CREATE TABLE IF NOT EXISTS `emo_questionnaire` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问卷编号',
  `title` varchar(255) NOT NULL COMMENT '问卷标题',
  `description` text COMMENT '问卷描述',
  `link` varchar(500) NOT NULL COMMENT '问卷链接',
  `type` tinyint NOT NULL COMMENT '问卷类型',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '问卷状态：0-草稿 1-已发布 2-已下线 3-已归档',
  `target_audience` varchar(200) COMMENT '目标人群',
  `estimated_duration` int COMMENT '预计时长（分钟）',
  `access_count` int DEFAULT 0 COMMENT '访问次数',
  `completion_count` int DEFAULT 0 COMMENT '完成次数',
  `is_open` bit(1) DEFAULT b'1' COMMENT '是否开放',
  `valid_from` datetime COMMENT '有效期开始时间',
  `valid_to` datetime COMMENT '有效期结束时间',
  `remark` text COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问卷表';

-- 测评表
CREATE TABLE IF NOT EXISTS `emo_assessment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '测评编号',
  `title` varchar(255) NOT NULL COMMENT '测评标题',
  `description` text COMMENT '测评描述',
  `type` tinyint NOT NULL COMMENT '测评类型',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '测评状态：0-草稿 1-已发布 2-已结束 3-已取消',
  `target_audience` varchar(200) COMMENT '目标人群',
  `duration` int COMMENT '测评时长（分钟）',
  `start_time` datetime COMMENT '开始时间',
  `end_time` datetime COMMENT '结束时间',
  `need_appointment` bit(1) DEFAULT b'0' COMMENT '是否需要预约',
  `max_participants` int COMMENT '最大参与人数',
  `current_participants` int DEFAULT 0 COMMENT '当前参与人数',
  `remark` text COMMENT '备注',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评表';

-- 测评问卷关联表
CREATE TABLE IF NOT EXISTS `emo_assessment_questionnaire` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联编号',
  `assessment_id` bigint NOT NULL COMMENT '测评ID',
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `is_required` bit(1) DEFAULT b'1' COMMENT '是否必填',
  `weight` decimal(5,2) DEFAULT 1.00 COMMENT '权重（用于计算总分）',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assessment_questionnaire` (`assessment_id`, `questionnaire_id`),
  KEY `idx_assessment_id` (`assessment_id`),
  KEY `idx_questionnaire_id` (`questionnaire_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评问卷关联表';

-- 测评结果表（用于存储整体测评结果）
CREATE TABLE IF NOT EXISTS `emo_assessment_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '结果编号',
  `assessment_id` bigint NOT NULL COMMENT '测评ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `overall_score` decimal(10,2) COMMENT '总体得分',
  `overall_level` varchar(50) COMMENT '总体评级',
  `overall_report` text COMMENT '总体测评报告',
  `completed_time` datetime COMMENT '完成时间',
  `status` tinyint DEFAULT 0 COMMENT '状态：0-进行中 1-已完成',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assessment_user` (`assessment_id`, `user_id`),
  KEY `idx_assessment_id` (`assessment_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_completed_time` (`completed_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测评结果表';

-- 问卷结果表（用于存储单个问卷的结果）
CREATE TABLE IF NOT EXISTS `emo_questionnaire_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '问卷结果编号',
  `assessment_result_id` bigint NOT NULL COMMENT '测评结果ID',
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID',
  `result_data` text COMMENT '问卷结果数据（JSON格式）',
  `score` decimal(10,2) COMMENT '问卷得分',
  `level` varchar(50) COMMENT '问卷评级',
  `report` text COMMENT '问卷报告',
  `completed_time` datetime COMMENT '完成时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_result_questionnaire` (`assessment_result_id`, `questionnaire_id`),
  KEY `idx_assessment_result_id` (`assessment_result_id`),
  KEY `idx_questionnaire_id` (`questionnaire_id`),
  KEY `idx_completed_time` (`completed_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问卷结果表';

-- 问卷访问记录表（可选，用于统计分析）
CREATE TABLE IF NOT EXISTS `emo_questionnaire_access` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问记录编号',
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID',
  `user_id` bigint COMMENT '用户ID',
  `access_token` varchar(64) COMMENT '访问令牌',
  `ip_address` varchar(45) COMMENT 'IP地址',
  `user_agent` varchar(500) COMMENT '用户代理',
  `access_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '访问时间',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_questionnaire_id` (`questionnaire_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_access_time` (`access_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='问卷访问记录表';

-- 插入测试数据
INSERT INTO `emo_questionnaire` (`id`, `title`, `description`, `link`, `type`, `status`, `target_audience`, `estimated_duration`, `access_count`, `completion_count`, `is_open`, `valid_from`, `valid_to`, `remark`, `creator`) VALUES
(1, '焦虑量表(SAS)', '用于评估焦虑水平的标准量表', 'https://example.com/questionnaire/sas', 4, 1, '学生', 10, 120, 95, b'1', '2024-01-01 00:00:00', '2024-12-31 23:59:59', '焦虑评估量表', 'admin'),
(2, 'SCL-90量表', '症状自评量表，用于心理健康筛查', 'https://example.com/questionnaire/scl90', 4, 1, '学生', 15, 80, 65, b'1', '2024-01-01 00:00:00', '2024-12-31 23:59:59', '心理健康筛查', 'admin'),
(3, '抑郁量表(SDS)', '用于评估抑郁程度的标准量表', 'https://example.com/questionnaire/sds', 4, 1, '学生', 8, 90, 72, b'1', '2024-01-01 00:00:00', '2024-12-31 23:59:59', '抑郁评估量表', 'admin'),
(4, '儿童发展测评问卷', '这是一个评估儿童发展状况的问卷', 'https://example.com/questionnaire/child', 1, 1, '3-6岁儿童', 15, 50, 35, b'1', '2024-01-01 00:00:00', '2024-12-31 23:59:59', '儿童发展测试', 'admin');

INSERT INTO `emo_assessment` (`id`, `title`, `description`, `type`, `status`, `target_audience`, `duration`, `start_time`, `end_time`, `need_appointment`, `max_participants`, `current_participants`, `remark`, `creator`) VALUES
(1, '入学常规测评', '新生入学心理健康常规测评，包含焦虑和心理健康筛查', 4, 1, '新入学学生', 30, '2024-09-01 09:00:00', '2024-09-30 18:00:00', b'1', 500, 125, '新生入学测评', 'admin'),
(2, '期中心理健康评估', '期中阶段学生心理健康状况评估', 4, 1, '在校学生', 25, '2024-11-01 09:00:00', '2024-11-15 18:00:00', b'0', 300, 80, '期中评估', 'admin'),
(3, '儿童发展专项测评', '针对3-6岁儿童的发展状况专项测评', 1, 1, '3-6岁儿童', 20, '2024-10-01 09:00:00', '2024-10-31 18:00:00', b'1', 100, 35, '儿童发展测评', 'admin');

-- 测评问卷关联数据
INSERT INTO `emo_assessment_questionnaire` (`assessment_id`, `questionnaire_id`, `sort_order`, `is_required`, `weight`, `creator`) VALUES
-- 入学常规测评包含焦虑量表和SCL-90量表
(1, 1, 1, b'1', 0.40, 'admin'), -- 焦虑量表，权重40%
(1, 2, 2, b'1', 0.60, 'admin'), -- SCL-90量表，权重60%
-- 期中心理健康评估包含焦虑量表、SCL-90量表和抑郁量表
(2, 1, 1, b'1', 0.30, 'admin'), -- 焦虑量表，权重30%
(2, 2, 2, b'1', 0.40, 'admin'), -- SCL-90量表，权重40%
(2, 3, 3, b'1', 0.30, 'admin'), -- 抑郁量表，权重30%
-- 儿童发展专项测评只包含儿童发展问卷
(3, 4, 1, b'1', 1.00, 'admin'); -- 儿童发展问卷，权重100%

-- ----------------------------
-- 1、宝宝测评附件表
-- ----------------------------
DROP TABLE IF EXISTS `baby_assessment_file`;
CREATE TABLE `baby_assessment_file`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `baby_id` bigint NOT NULL COMMENT '宝宝ID',
  `assessment_id` bigint NULL DEFAULT NULL COMMENT '测评ID（可为空，表示通用附件）',
  `file_id` bigint NOT NULL COMMENT '文件ID（关联infra_file表）',
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件名',
  `file_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件类型',
  `file_size` bigint NOT NULL DEFAULT 0 COMMENT '文件大小（字节）',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附件描述',
  `upload_user_id` bigint NOT NULL COMMENT '上传管理员ID',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_file_id`(`file_id` ASC) USING BTREE COMMENT '文件ID唯一索引',
  INDEX `idx_baby_id`(`baby_id` ASC) USING BTREE COMMENT '宝宝ID索引',
  INDEX `idx_assessment_id`(`assessment_id` ASC) USING BTREE COMMENT '测评ID索引',
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE COMMENT '租户索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '宝宝测评附件表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 2、添加外键约束（可选，根据实际需要）
-- ----------------------------
-- ALTER TABLE `baby_assessment_file` ADD CONSTRAINT `fk_baby_assessment_file_baby` FOREIGN KEY (`baby_id`) REFERENCES `member_baby` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `baby_assessment_file` ADD CONSTRAINT `fk_baby_assessment_file_assessment` FOREIGN KEY (`assessment_id`) REFERENCES `emo_assessment` (`id`) ON DELETE CASCADE;
-- ALTER TABLE `baby_assessment_file` ADD CONSTRAINT `fk_baby_assessment_file_file` FOREIGN KEY (`file_id`) REFERENCES `infra_file` (`id`) ON DELETE CASCADE;

-- ----------------------------
-- 3、初始化数据（可选）
-- ----------------------------
-- 这里可以添加一些测试数据
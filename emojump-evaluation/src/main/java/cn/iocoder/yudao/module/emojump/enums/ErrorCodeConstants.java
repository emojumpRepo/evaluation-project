package cn.iocoder.yudao.module.emojump.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Emojump 错误码枚举类
 *
 * emojump 系统，使用 1-008-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 测评管理 1-008-001-000 ==========
    ErrorCode ASSESSMENT_NOT_EXISTS = new ErrorCode(1_008_001_000, "测评不存在");
    ErrorCode ASSESSMENT_NOT_PUBLISHED = new ErrorCode(1_008_001_001, "测评未发布");
    ErrorCode ASSESSMENT_ALREADY_PUBLISHED = new ErrorCode(1_008_001_002, "测评已发布");
    ErrorCode ASSESSMENT_EXPIRED = new ErrorCode(1_008_001_003, "测评已过期");
    ErrorCode ASSESSMENT_FULL = new ErrorCode(1_008_001_004, "测评参与人数已满");
    ErrorCode ASSESSMENT_ALREADY_PARTICIPATED = new ErrorCode(1_008_001_005, "已参与该测评");

    // ========== 问卷管理 1-008-002-000 ==========
    ErrorCode QUESTIONNAIRE_NOT_EXISTS = new ErrorCode(1_008_002_000, "问卷不存在");
    ErrorCode QUESTIONNAIRE_NOT_PUBLISHED = new ErrorCode(1_008_002_001, "问卷未发布");
    ErrorCode QUESTIONNAIRE_ALREADY_PUBLISHED = new ErrorCode(1_008_002_002, "问卷已发布");
    ErrorCode QUESTIONNAIRE_EXPIRED = new ErrorCode(1_008_002_003, "问卷已过期");
    ErrorCode QUESTIONNAIRE_LINK_INVALID = new ErrorCode(1_008_002_004, "问卷链接无效");

    //========== 会员文章 1-008-003-000 ==========
    ErrorCode ARTICLE_NOT_EXISTS = new ErrorCode(1_008_003_000, "会员文章不存在");

} 

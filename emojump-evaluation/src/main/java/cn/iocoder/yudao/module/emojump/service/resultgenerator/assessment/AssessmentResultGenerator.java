package cn.iocoder.yudao.module.emojump.service.resultgenerator.assessment;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.assessment.AssessmentResultDTO;

import java.util.List;

/**
 * 测评结果生成器接口
 * 
 * @author 芋道源码
 */
public interface AssessmentResultGenerator {

    /**
     * 获取支持的测评ID列表
     *
     * @return 测评ID列表
     */
    List<Long> getSupportedAssessmentIds();

    /**
     * 获取支持的测评ID（兼容旧版本，返回第一个）
     *
     * @return 测评ID
     */
    default Long getSupportedAssessmentId() {
        List<Long> ids = getSupportedAssessmentIds();
        return ids != null && !ids.isEmpty() ? ids.get(0) : null;
    }

    /**
     * 获取测评名称（用于日志和调试）
     *
     * @return 测评名称
     */
    String getAssessmentName();

    /**
     * 生成测评结果
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @param questionnaireResults 问卷结果列表
     * @return 测评结果（包含resultData和report）
     */
    AssessmentResultDTO generateResult(Long assessmentId, Long babyId, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults);

    /**
     * 验证问卷结果数据是否有效
     *
     * @param questionnaireResults 问卷结果列表
     * @return 是否有效
     */
    default boolean validateQuestionnaireResults(List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        return questionnaireResults != null && !questionnaireResults.isEmpty();
    }
} 
package cn.iocoder.yudao.module.emojump.service.resultgenerator.assessment;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.assessment.AssessmentResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 测评结果生成器抽象基类
 *
 * @author 芋道源码
 */
@Slf4j
public abstract class AbstractAssessmentResultGenerator implements AssessmentResultGenerator {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public AssessmentResultDTO generateResult(Long assessmentId, Long babyId, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        log.info("[generateResult] 开始生成测评结果，测评ID: {}, 宝宝ID: {}, 问卷结果数量: {}", 
                assessmentId, babyId, questionnaireResults != null ? questionnaireResults.size() : 0);

        try {
            // 1. 验证输入数据
            if (!validateInput(assessmentId, babyId, questionnaireResults)) {
                throw new RuntimeException("输入数据验证失败");
            }

            // 2. 计算测评结果
            AssessmentResultDTO result = calculateResult(assessmentId, babyId, questionnaireResults);

            // 3. 构建结果数据
            String resultData = buildResultData(result);
            result.setResultData(resultData);

            log.info("[generateResult] 测评结果生成成功，测评ID: {}, 总分: {}, 评级: {}", 
                    assessmentId, result.getOverallScore(), result.getOverallLevel());

            return result;

        } catch (Exception e) {
            log.error("[generateResult] 测评结果生成失败，测评ID: {}, 错误: {}", assessmentId, e.getMessage(), e);
            throw new RuntimeException("测评结果生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证输入数据
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @param questionnaireResults 问卷结果列表
     * @return 是否有效
     */
    protected boolean validateInput(Long assessmentId, Long babyId, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        if (assessmentId == null || assessmentId <= 0) {
            log.error("[validateInput] 测评ID无效: {}", assessmentId);
            return false;
        }

        if (babyId == null || babyId <= 0) {
            log.error("[validateInput] 宝宝ID无效: {}", babyId);
            return false;
        }

        if (!validateQuestionnaireResults(questionnaireResults)) {
            log.error("[validateInput] 问卷结果数据无效");
            return false;
        }

        return true;
    }

    /**
     * 计算测评结果（子类实现具体逻辑）
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @param questionnaireResults 问卷结果列表
     * @return 测评结果
     */
    protected abstract AssessmentResultDTO calculateResult(Long assessmentId, Long babyId, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults);

    /**
     * 构建结果数据JSON字符串
     *
     * @param result 测评结果
     * @return JSON字符串
     */
    protected String buildResultData(AssessmentResultDTO result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("[buildResultData] 构建结果数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("构建结果数据失败", e);
        }
    }

    /**
     * 计算总体得分
     *
     * @param questionnaireResults 问卷结果列表
     * @return 总体得分
     */
    protected double calculateOverallScore(List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        if (questionnaireResults == null || questionnaireResults.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        int validCount = 0;

        for (AssessmentResultDTO.QuestionnaireResultItem item : questionnaireResults) {
            if (item.getScore() != null) {
                totalScore += item.getScore().doubleValue();
                validCount++;
            }
        }

        return validCount > 0 ? totalScore / validCount : 0.0;
    }

    /**
     * 确定总体评级
     *
     * @param overallScore 总体得分
     * @return 评级
     */
    protected String determineOverallLevel(double overallScore) {
        // 默认评级逻辑，子类可以重写
        if (overallScore >= 80) {
            return "优秀";
        } else if (overallScore >= 60) {
            return "良好";
        } else if (overallScore >= 40) {
            return "一般";
        } else {
            return "需要关注";
        }
    }
} 
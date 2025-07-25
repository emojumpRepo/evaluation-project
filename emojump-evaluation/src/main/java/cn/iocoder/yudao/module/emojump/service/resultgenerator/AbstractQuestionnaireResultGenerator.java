package cn.iocoder.yudao.module.emojump.service.resultgenerator;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问卷结果生成器抽象基类
 * 提供通用的功能和模板方法
 *
 * @author 芋道源码
 */
@Slf4j
public abstract class AbstractQuestionnaireResultGenerator implements QuestionnaireResultGenerator {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public final QuestionnaireResultDTO generateResult(QuestionnaireAnswerDTO answerDTO) {
        log.info("[{}] 开始生成问卷结果，答案数量: {}",
                getQuestionnaireName(), answerDTO.getAnswers().size());

        try {
            // 1. 验证答案数据
            if (!validateAnswerData(answerDTO)) {
                throw new RuntimeException("问卷答案数据无效");
            }

            // 2. 计算结果
            QuestionnaireResultDTO result = calculateResult(answerDTO);

            // 3. 生成结果数据JSON
            String resultDataJson = generateResultDataJson(result);
            result.setResultData(resultDataJson);

            // 4. 生成报告内容（富文本格式，适合小程序显示）
            String reportContent = generateReportHtml(result, answerDTO);
            result.setReport(reportContent);

            log.info("[{}] 问卷结果生成成功，总分: {}, 评级: {}",
                    getQuestionnaireName(), result.getScore(), result.getLevel());

            return result;

        } catch (Exception e) {
            log.error("[{}] 问卷结果生成失败: {}", getQuestionnaireName(), e.getMessage(), e);
            throw new RuntimeException("问卷结果生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 计算问卷结果（子类实现）
     *
     * @param answerDTO 问卷答案
     * @return 问卷结果
     */
    protected abstract QuestionnaireResultDTO calculateResult(QuestionnaireAnswerDTO answerDTO);

    /**
     * 生成结果数据JSON
     *
     * @param result 问卷结果
     * @return JSON字符串
     */
    protected String generateResultDataJson(QuestionnaireResultDTO result) {
        try {
            Map<String, Object> resultData = new HashMap<>();
            
            // 添加摘要信息
            if (result.getSummary() != null) {
                resultData.put("summary", result.getSummary());
            }
            
            // 添加详细信息
            if (result.getDetails() != null && !result.getDetails().isEmpty()) {
                resultData.put("details", result.getDetails());
            }

            return objectMapper.writeValueAsString(resultData);
        } catch (Exception e) {
            log.error("[{}] 生成结果数据JSON失败: {}", getQuestionnaireName(), e.getMessage(), e);
            throw new RuntimeException("生成结果数据JSON失败", e);
        }
    }

    /**
     * 生成报告HTML（子类实现）
     *
     * @param result 问卷结果
     * @param answerDTO 问卷答案
     * @return HTML字符串
     */
    protected abstract String generateReportHtml(QuestionnaireResultDTO result, QuestionnaireAnswerDTO answerDTO);

    /**
     * 根据分数获取评级
     *
     * @param score 分数
     * @param thresholds 阈值配置 [低风险上限, 中风险上限]
     * @return 评级
     */
    protected String getLevelByScore(int score, int[] thresholds) {
        if (score <= thresholds[0]) {
            return "低风险";
        } else if (score <= thresholds[1]) {
            return "中度风险";
        } else {
            return "高风险";
        }
    }

    /**
     * 根据评级获取解释
     *
     * @param level 评级
     * @return 解释文本
     */
    protected String getInterpretationByLevel(String level) {
        switch (level) {
            case "低风险":
                return "得分在正常范围内，表现良好。";
            case "中度风险":
                return "得分略高，建议关注并采取适当措施。";
            case "高风险":
                return "得分较高，建议寻求专业指导。";
            default:
                return "评估结果需要进一步分析。";
        }
    }

    /**
     * 计算答案总分
     *
     * @param answers 答案列表
     * @return 总分
     */
    protected int calculateTotalScore(List<QuestionnaireAnswerDTO.AnswerItem> answers) {
        return answers.stream()
                .mapToInt(answer -> answer.getScore() != null ? answer.getScore() : 0)
                .sum();
    }

    /**
     * 根据维度配置生成详细结果（支持自定义阈值）
     *
     * @param dimensionScores 各维度分数
     * @param dimensionThresholds 各维度阈值配置
     * @return 详细结果列表
     */
    protected List<QuestionnaireResultDTO.ResultDetail> generateDetailsWithCustomThresholds(
            Map<String, Integer> dimensionScores,
            Map<String, int[]> dimensionThresholds) {

        List<QuestionnaireResultDTO.ResultDetail> details = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
            String dimension = entry.getKey();
            Integer score = entry.getValue();

            // 获取该维度的自定义阈值，如果没有配置则使用默认阈值
            int[] thresholds = dimensionThresholds.getOrDefault(dimension, new int[]{1, 3});

            // 使用维度特定的阈值计算评级
            String level = getLevelByScore(score, thresholds);

            // 生成基础解释（子类可以重写以提供更专业的解释）
            String interpretation = getDimensionInterpretationWithThreshold(dimension, level, score, thresholds);

            QuestionnaireResultDTO.ResultDetail detail = QuestionnaireResultDTO.ResultDetail.builder()
                    .label(dimension)
                    .value(score)
                    .level(level)
                    .interpretation(interpretation)
                    .build();

            details.add(detail);
        }

        return details;
    }

    /**
     * 获取维度解释（包含阈值信息）
     *
     * @param dimension 维度名称
     * @param level 评级
     * @param score 得分
     * @param thresholds 阈值配置
     * @return 解释文本
     */
    protected String getDimensionInterpretationWithThreshold(String dimension, String level, int score, int[] thresholds) {
        if ("低风险".equals(level)) {
            return String.format("%s得分为%d分（≤%d分），表现良好，在正常范围内。",
                    dimension, score, thresholds[0]);
        } else if ("中度风险".equals(level)) {
            return String.format("%s得分为%d分（%d-%d分），存在一定程度的困难，建议给予关注。",
                    dimension, score, thresholds[0] + 1, thresholds[1]);
        } else {
            return String.format("%s得分为%d分（>%d分），存在较明显的困难，建议寻求专业指导。",
                    dimension, score, thresholds[1]);
        }
    }
}

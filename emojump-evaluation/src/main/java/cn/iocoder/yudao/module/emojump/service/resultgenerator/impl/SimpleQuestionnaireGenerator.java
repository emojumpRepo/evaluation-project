package cn.iocoder.yudao.module.emojump.service.resultgenerator.impl;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.AbstractQuestionnaireResultGenerator;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireResultDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单问卷结果生成器示例
 * 问卷ID: 2001 (示例)
 * 
 * 这是一个简化的示例，展示如何快速创建新的问卷生成器
 *
 * @author 芋道源码
 */
@Component
public class SimpleQuestionnaireGenerator extends AbstractQuestionnaireResultGenerator {

    private static final Long QUESTIONNAIRE_ID = 2001L;
    private static final String QUESTIONNAIRE_NAME = "简单评估问卷";

    // 简单的评分规则
    private static final Map<String, Integer> ANSWER_SCORES = new HashMap<>();
    static {
        ANSWER_SCORES.put("完全不符合", 1);
        ANSWER_SCORES.put("基本不符合", 2);
        ANSWER_SCORES.put("一般", 3);
        ANSWER_SCORES.put("比较符合", 4);
        ANSWER_SCORES.put("完全符合", 5);
    }

    // 维度阈值配置 - 每个维度有不同的评级标准
    private static final Map<String, int[]> DIMENSION_THRESHOLDS = new HashMap<>();
    static {
        // 格式：{低风险上限, 中度风险上限} - 超过中度风险上限为高风险
        DIMENSION_THRESHOLDS.put("认知能力", new int[]{6, 12});   // 假设3题×1-5分，低风险≤6，中度风险7-12，高风险>12
        DIMENSION_THRESHOLDS.put("情感发展", new int[]{8, 15});   // 假设4题×1-5分，低风险≤8，中度风险9-15，高风险>15
        DIMENSION_THRESHOLDS.put("行为表现", new int[]{7, 14});   // 假设3题×1-5分，低风险≤7，中度风险8-14，高风险>14
    }

    @Override
    public Long getSupportedQuestionnaireId() {
        return QUESTIONNAIRE_ID;
    }

    @Override
    public String getQuestionnaireName() {
        return QUESTIONNAIRE_NAME;
    }

    @Override
    protected QuestionnaireResultDTO calculateResult(QuestionnaireAnswerDTO answerDTO) {
        // 1. 计算总分
        int totalScore = 0;
        for (QuestionnaireAnswerDTO.AnswerItem answer : answerDTO.getAnswers()) {
            Integer score = ANSWER_SCORES.getOrDefault(answer.getAnswer(), 1);
            totalScore += score;
        }

        // 2. 确定评级
        String level = getLevelByScore(totalScore, new int[]{20, 40}); // 简单阈值

        // 3. 生成摘要
        int[] range = getSummaryRange(level);
        String description = getSummaryDescription(level);
        QuestionnaireResultDTO.Advice advice = generateSummaryAdvice(level);

        QuestionnaireResultDTO.ResultSummary summary = QuestionnaireResultDTO.ResultSummary.builder()
                .label("总分")
                .value(totalScore)
                .level(level)
                .range(range)
                .description(description)
                .interpretation(getSimpleInterpretation(level, totalScore))
                .advice(advice)
                .build();

        // 4. 生成详细结果（简化版）
        List<QuestionnaireResultDTO.ResultDetail> details = generateSimpleDetails(totalScore);

        return QuestionnaireResultDTO.builder()
                .score(BigDecimal.valueOf(totalScore))
                .level(level)
                .summary(summary)
                .details(details)
                .build();
    }

    /**
     * 生成简单的解释
     */
    private String getSimpleInterpretation(String level, int totalScore) {
        switch (level) {
            case "低风险":
                return String.format("您的总分为%d分，表现优秀，各方面发展良好。", totalScore);
            case "中度风险":
                return String.format("您的总分为%d分，整体表现良好，部分方面可以进一步提升。", totalScore);
            case "高风险":
                return String.format("您的总分为%d分，建议关注相关方面的发展，必要时寻求专业指导。", totalScore);
            default:
                return String.format("您的总分为%d分，评估结果需要进一步分析。", totalScore);
        }
    }

    /**
     * 生成简单的详细结果（使用维度特定阈值）
     */
    private List<QuestionnaireResultDTO.ResultDetail> generateSimpleDetails(int totalScore) {
        // 模拟各维度分数
        Map<String, Integer> dimensionScores = new HashMap<>();
        dimensionScores.put("认知能力", Math.max(1, totalScore / 3 - 1));
        dimensionScores.put("情感发展", totalScore / 3);
        dimensionScores.put("行为表现", Math.max(1, totalScore / 3 + 1));

        // 使用抽象基类提供的方法，支持维度特定阈值
        return generateDetailsWithCustomThresholds(dimensionScores, DIMENSION_THRESHOLDS);
    }

    /**
     * 获取总分的阈值范围
     */
    private int[] getSummaryRange(String level) {
        if ("低风险".equals(level)) {
            return new int[]{0, 5};  // 0-5分
        } else if ("中度风险".equals(level)) {
            return new int[]{6, 10};  // 6-10分
        } else {
            return new int[]{11, 20};  // 11-20分
        }
    }

    /**
     * 获取总分的描述
     */
    private String getSummaryDescription(String level) {
        if ("低风险".equals(level)) {
            return "评估结果在正常范围内，各项指标表现良好。";
        } else if ("中度风险".equals(level)) {
            return "评估结果存在一定程度的问题，需要适当关注。";
        } else {
            return "评估结果显示存在较明显的问题，建议寻求专业帮助。";
        }
    }

    /**
     * 生成总分的建议
     */
    private QuestionnaireResultDTO.Advice generateSummaryAdvice(String level) {
        List<String> content = new ArrayList<>();
        String description;

        if ("低风险".equals(level)) {
            description = "维护良好状态的建议";
            content.add("继续保持当前的良好状态");
            content.add("定期进行自我评估和调整");
            content.add("适当挑战自己，促进进一步发展");
            content.add("保持积极的生活态度");
            content.add("建立良好的支持网络");
        } else if ("中度风险".equals(level)) {
            description = "改善和提升的建议";
            content.add("重点关注得分较低的维度");
            content.add("制定针对性的改进计划");
            content.add("寻求适当的支持和指导");
            content.add("增加相关技能的学习和练习");
            content.add("定期监测进展情况");
        } else {
            description = "专业干预的建议";
            content.add("立即寻求相关领域的专业人士帮助");
            content.add("制定系统性的干预方案");
            content.add("定期跟踪评估进展情况");
            content.add("建立强有力的支持系统");
            content.add("考虑专业治疗或训练项目");
        }

        return QuestionnaireResultDTO.Advice.builder()
                .description(description)
                .content(content)
                .build();
    }

}

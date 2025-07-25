package cn.iocoder.yudao.module.emojump.service.resultgenerator.impl;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.AbstractQuestionnaireResultGenerator;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireResultDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 儿童适应能力测评量表结果生成器
 * 问卷ID: 18
 *
 * @author 芋道源码
 */
@Component
public class ChildAdaptiveAbilityQuestionnaireGenerator extends AbstractQuestionnaireResultGenerator {

    private static final Long QUESTIONNAIRE_ID = 18L;
    private static final String QUESTIONNAIRE_NAME = "儿童适应能力测评量表";

    // 评分规则配置
    private static final Map<String, Integer> ANSWER_SCORES = new HashMap<>();
    static {
        ANSWER_SCORES.put("能做到", 3);
        ANSWER_SCORES.put("不能做到", 0);
    }

    // 维度配置（假设24题，分为4个维度，每个维度6题）
    private static final Map<String, int[]> DIMENSION_QUESTIONS = new HashMap<>();
    static {
        DIMENSION_QUESTIONS.put("日常生活技能", new int[]{1, 2, 3, 4, 5, 6});
        DIMENSION_QUESTIONS.put("社交沟通能力", new int[]{7, 8, 9, 10, 11, 12});
        DIMENSION_QUESTIONS.put("学习适应能力", new int[]{13, 14, 15, 16, 17, 18});
        DIMENSION_QUESTIONS.put("情绪调节能力", new int[]{19, 20, 21, 22, 23, 24});
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
        // 1. 计算各题目分数
        List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers = calculateAnswerScores(answerDTO.getAnswers());

        // 2. 计算总分
        int totalScore = calculateTotalScore(scoredAnswers);

        // 3. 确定评级
        String level = getAdaptiveAbilityLevel(totalScore);

        // 4. 生成结果摘要
        QuestionnaireResultDTO.ResultSummary summary = generateSummary(totalScore, level);

        // 5. 生成详细结果
        List<QuestionnaireResultDTO.ResultDetail> details = generateDetails(totalScore, level, scoredAnswers);

        return QuestionnaireResultDTO.builder()
                .score(BigDecimal.valueOf(totalScore))
                .level(level)
                .summary(summary)
                .details(details)
                .build();
    }

    /**
     * 计算答案分数
     */
    private List<QuestionnaireAnswerDTO.AnswerItem> calculateAnswerScores(List<QuestionnaireAnswerDTO.AnswerItem> answers) {
        List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers = new ArrayList<>();

        for (QuestionnaireAnswerDTO.AnswerItem answer : answers) {
            Integer score = ANSWER_SCORES.get(answer.getAnswer());
            if (score == null) {
                score = 0; // 默认分数
            }

            QuestionnaireAnswerDTO.AnswerItem scoredAnswer = QuestionnaireAnswerDTO.AnswerItem.builder()
                    .title(answer.getTitle())
                    .answer(answer.getAnswer())
                    .index(answer.getIndex())
                    .score(score)
                    .build();

            scoredAnswers.add(scoredAnswer);
        }

        return scoredAnswers;
    }

    /**
     * 根据总分确定适应能力水平
     */
    private String getAdaptiveAbilityLevel(int totalScore) {
        // 总分范围：0-72分（24题 × 3分）
        if (totalScore >= 60) {
            return "优秀";
        } else if (totalScore >= 48) {
            return "良好";
        } else if (totalScore >= 36) {
            return "一般";
        } else {
            return "需要提升";
        }
    }

    /**
     * 生成结果摘要
     */
    private QuestionnaireResultDTO.ResultSummary generateSummary(int totalScore, String level) {
        String interpretation = getAdaptiveAbilityInterpretation(totalScore, level);
        int[] range = getSummaryRange(level);
        String description = getSummaryDescription(level);
        QuestionnaireResultDTO.Advice advice = generateSummaryAdvice(level);

        return QuestionnaireResultDTO.ResultSummary.builder()
                .label("总分")
                .value(totalScore)
                .level(level)
                .range(range)
                .description(description)
                .interpretation(interpretation)
                .advice(advice)
                .build();
    }

    /**
     * 获取适应能力发展解释
     */
    private String getAdaptiveAbilityInterpretation(int totalScore, String level) {
        switch (level) {
            case "优秀":
                return String.format("总分为%d分（≥60分），适应能力发展优秀，各项生活技能掌握良好，能够很好地适应环境变化。", totalScore);
            case "良好":
                return String.format("总分为%d分（48-59分），适应能力发展良好，大部分生活技能掌握较好，适应能力较强。", totalScore);
            case "一般":
                return String.format("总分为%d分（36-47分），适应能力发展一般，部分技能需要加强训练和指导。", totalScore);
            default:
                return String.format("总分为%d分（<36分），适应能力发展需要提升，建议加强相关训练和专业指导。", totalScore);
        }
    }

    /**
     * 生成详细结果
     */
    private List<QuestionnaireResultDTO.ResultDetail> generateDetails(int totalScore, String level,
                                                                    List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers) {
        List<QuestionnaireResultDTO.ResultDetail> details = new ArrayList<>();

        // 分析各维度
        for (Map.Entry<String, int[]> entry : DIMENSION_QUESTIONS.entrySet()) {
            String dimension = entry.getKey();
            int[] questions = entry.getValue();

            int dimensionScore = calculateDimensionScore(scoredAnswers, questions);
            String dimensionLevel = getDimensionLevel(dimensionScore, questions.length);

            details.add(createDimensionDetail(dimension, dimensionScore, dimensionLevel, questions.length));
        }

        // 整体适应能力评估
        details.add(createOverallAdaptationDetail(totalScore, level));

        return details;
    }

    /**
     * 计算维度得分
     */
    private int calculateDimensionScore(List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers, int[] questions) {
        int score = 0;
        for (int questionIndex : questions) {
            for (QuestionnaireAnswerDTO.AnswerItem answer : scoredAnswers) {
                if (answer.getIndex() == questionIndex) {
                    score += answer.getScore() != null ? answer.getScore() : 0;
                    break;
                }
            }
        }
        return score;
    }

    /**
     * 获取维度评级
     */
    private String getDimensionLevel(int score, int questionCount) {
        int maxScore = questionCount * 3;
        double percentage = (double) score / maxScore;

        if (percentage >= 0.83) {  // ≥83%
            return "优秀";
        } else if (percentage >= 0.67) {  // 67%-82%
            return "良好";
        } else if (percentage >= 0.50) {  // 50%-66%
            return "一般";
        } else {  // <50%
            return "需要提升";
        }
    }

    /**
     * 创建维度详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createDimensionDetail(String dimension, int score, String level, int questionCount) {
        int maxScore = questionCount * 3;
        int[] range = getDimensionRange(level, maxScore);
        String interpretation = getDimensionInterpretation(dimension, score, level, maxScore);

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label(dimension)
                .value(score)
                .level(level)
                .range(range)
                .interpretation(interpretation)
                .build();
    }

    /**
     * 创建整体适应能力评估详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createOverallAdaptationDetail(int totalScore, String level) {
        int[] range = getSummaryRange(level);
        String interpretation = getOverallAdaptationInterpretation(totalScore, level);

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("整体适应能力评估")
                .value(totalScore)
                .level(level)
                .range(range)
                .interpretation(interpretation)
                .build();
    }

    /**
     * 获取维度阈值范围
     */
    private int[] getDimensionRange(String level, int maxScore) {
        switch (level) {
            case "优秀":
                return new int[]{(int)(maxScore * 0.83), maxScore};
            case "良好":
                return new int[]{(int)(maxScore * 0.67), (int)(maxScore * 0.82)};
            case "一般":
                return new int[]{(int)(maxScore * 0.50), (int)(maxScore * 0.66)};
            default:
                return new int[]{0, (int)(maxScore * 0.49)};
        }
    }

    /**
     * 获取维度解释
     */
    private String getDimensionInterpretation(String dimension, int score, String level, int maxScore) {
        switch (level) {
            case "优秀":
                return String.format("%s得分为%d分（满分%d分），表现优秀，该领域适应能力强。", dimension, score, maxScore);
            case "良好":
                return String.format("%s得分为%d分（满分%d分），表现良好，大部分技能掌握较好。", dimension, score, maxScore);
            case "一般":
                return String.format("%s得分为%d分（满分%d分），表现一般，部分技能需要加强练习。", dimension, score, maxScore);
            default:
                return String.format("%s得分为%d分（满分%d分），需要提升，建议重点关注该领域的训练。", dimension, score, maxScore);
        }
    }

    /**
     * 获取整体适应能力解释
     */
    private String getOverallAdaptationInterpretation(int totalScore, String level) {
        switch (level) {
            case "优秀":
                return String.format("综合评估显示总分为%d分，适应能力发展优秀，各项技能均达到良好水平，能够很好地适应各种环境。", totalScore);
            case "良好":
                return String.format("综合评估显示总分为%d分，适应能力发展良好，可通过针对性训练进一步提升。", totalScore);
            case "一般":
                return String.format("综合评估显示总分为%d分，适应能力发展一般，建议加强日常训练和专业指导。", totalScore);
            default:
                return String.format("综合评估显示总分为%d分，适应能力发展需要提升，建议寻求专业的训练指导。", totalScore);
        }
    }

    /**
     * 获取总分的阈值范围
     */
    private int[] getSummaryRange(String level) {
        switch (level) {
            case "优秀":
                return new int[]{60, 72};
            case "良好":
                return new int[]{48, 59};
            case "一般":
                return new int[]{36, 47};
            default:
                return new int[]{0, 35};
        }
    }

    /**
     * 获取总分的描述
     */
    private String getSummaryDescription(String level) {
        switch (level) {
            case "优秀":
                return "孩子的适应能力发展优秀，各项生活技能掌握良好，能够很好地适应环境变化。";
            case "良好":
                return "孩子的适应能力发展良好，大部分生活技能掌握较好，适应能力较强。";
            case "一般":
                return "孩子的适应能力发展一般，部分技能需要加强训练和指导。";
            default:
                return "孩子的适应能力发展需要提升，建议加强相关训练和专业指导。";
        }
    }

    /**
     * 生成总分的建议
     */
    private QuestionnaireResultDTO.Advice generateSummaryAdvice(String level) {
        List<String> content = new ArrayList<>();
        String description;

        switch (level) {
            case "优秀":
                description = "维护和发展适应能力的建议";
                content.add("继续提供丰富的生活技能学习机会");
                content.add("鼓励孩子尝试新的环境和挑战");
                content.add("培养孩子的独立性和自主性");
                content.add("定期评估发展状况，保持技能水平");
                content.add("可以承担更多的家庭和学校责任");
                break;
            case "良好":
                description = "进一步提升适应能力的建议";
                content.add("增加日常生活技能练习的机会");
                content.add("重点关注得分较低的维度进行针对性训练");
                content.add("提供适合年龄的独立完成任务的机会");
                content.add("鼓励参与团体活动，提升社交适应能力");
                content.add("与老师配合，在学校也进行相关练习");
                break;
            case "一般":
                description = "加强适应能力训练的建议";
                content.add("制定系统的生活技能训练计划");
                content.add("从基础的日常生活技能开始练习");
                content.add("提供结构化的学习和练习环境");
                content.add("寻求特殊教育或康复专家的指导");
                content.add("每天安排固定时间进行技能练习");
                break;
            default:
                description = "专业干预和康复训练的建议";
                content.add("立即寻求儿童发展专家或特殊教育专家的评估");
                content.add("制定个性化的适应能力训练方案");
                content.add("考虑专业的适应行为训练课程");
                content.add("家庭、学校和专业机构密切配合");
                content.add("定期监测进展，调整训练方案");
                content.add("必要时进行全面的发育评估");
                break;
        }

        return QuestionnaireResultDTO.Advice.builder()
                .description(description)
                .content(content)
                .build();
    }

}
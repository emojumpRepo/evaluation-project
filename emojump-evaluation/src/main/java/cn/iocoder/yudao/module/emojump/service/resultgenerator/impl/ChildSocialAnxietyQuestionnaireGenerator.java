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
 * 儿童社交焦虑量表（SASC）结果生成器
 * 问卷ID: 16
 *
 * @author 芋道源码
 */
@Component
public class ChildSocialAnxietyQuestionnaireGenerator extends AbstractQuestionnaireResultGenerator {

    private static final Long QUESTIONNAIRE_ID = 16L;
    private static final String QUESTIONNAIRE_NAME = "儿童社交焦虑量表（SASC）";

    // 评分规则配置
    private static final Map<String, Integer> ANSWER_SCORES = new HashMap<>(); // key: 答案选项, value: 分数
    static {
        ANSWER_SCORES.put("从不是这样", 0);
        ANSWER_SCORES.put("有时这样", 1);
        ANSWER_SCORES.put("一直这样", 2);
    }

    // 维度配置
    private static final Map<String, int[]> DIMENSION_QUESTIONS = new HashMap<>(); // key: 维度名称, value: 题目索引数组
    static {
        DIMENSION_QUESTIONS.put("害怕否定评价", new int[]{1, 2, 5, 6, 8, 10});
        DIMENSION_QUESTIONS.put("社交回避及苦恼", new int[]{3, 4, 7, 9});
    }

    // 维度阈值配置 - 每个维度有不同的评级标准
    private static final Map<String, int[]> DIMENSION_THRESHOLDS = new HashMap<>();
    static {
        // 格式：{低风险上限, 中度风险上限} - 超过中度风险上限为高风险
        DIMENSION_THRESHOLDS.put("害怕否定评价", new int[]{3, 8});   // 6题×0-2分 = 0-12分
        DIMENSION_THRESHOLDS.put("社交回避及苦恼", new int[]{2, 5});  // 4题×0-2分 = 0-8分
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

        // 3. 计算各维度分数
        Map<String, Integer> dimensionScores = calculateDimensionScores(scoredAnswers);

        // 4. 生成结果摘要
        QuestionnaireResultDTO.ResultSummary summary = generateSummary(totalScore);

        // 5. 生成详细结果
        List<QuestionnaireResultDTO.ResultDetail> details = generateDetails(dimensionScores);

        return QuestionnaireResultDTO.builder()
                .score(BigDecimal.valueOf(totalScore))
                .level(summary.getLevel())
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
     * 计算各维度分数
     */
    private Map<String, Integer> calculateDimensionScores(List<QuestionnaireAnswerDTO.AnswerItem> answers) {
        Map<String, Integer> dimensionScores = new HashMap<>();

        for (Map.Entry<String, int[]> entry : DIMENSION_QUESTIONS.entrySet()) {
            String dimension = entry.getKey();
            int[] questionIndexes = entry.getValue();

            int dimensionScore = 0;
            for (int index : questionIndexes) {
                for (QuestionnaireAnswerDTO.AnswerItem answer : answers) {
                    if (answer.getIndex() != null && answer.getIndex() == index) {
                        dimensionScore += answer.getScore() != null ? answer.getScore() : 0;
                        break;
                    }
                }
            }

            dimensionScores.put(dimension, dimensionScore);
        }

        return dimensionScores;
    }

    /**
     * 生成结果摘要
     */
    private QuestionnaireResultDTO.ResultSummary generateSummary(int totalScore) {
        String level = getLevelByScore(totalScore, new int[]{8, 16}); // 低风险≤8, 中度风险≤16, 高风险>16
        String interpretation = getInterpretationByLevel(level);
        
        if ("中度风险".equals(level)) {
            interpretation = "总分较高，表明被评估者在整体适应性行为上可能存在中度困难，建议您特别关注。";
        } else if ("高风险".equals(level)) {
            interpretation = "总分很高，表明被评估者在适应性行为上存在明显困难，建议寻求专业帮助。";
        } else {
            interpretation = "总分在正常范围内，整体适应性行为表现良好。";
        }

        // 生成新字段
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
     * 生成详细结果
     */
    private List<QuestionnaireResultDTO.ResultDetail> generateDetails(Map<String, Integer> dimensionScores) {
        List<QuestionnaireResultDTO.ResultDetail> details = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
            String dimension = entry.getKey();
            Integer score = entry.getValue();

            // 获取该维度的自定义阈值，如果没有配置则使用默认阈值
            int[] thresholds = DIMENSION_THRESHOLDS.getOrDefault(dimension, new int[]{1, 3});

            // 使用维度特定的阈值计算评级
            String level = getLevelByScore(score, thresholds);

            // 生成该维度的解释文本
            String interpretation = getDimensionInterpretation(dimension, level, score, thresholds);

            // 获取该维度的阈值范围
            int[] range = getDimensionRange(dimension, level, thresholds);

            QuestionnaireResultDTO.ResultDetail detail = QuestionnaireResultDTO.ResultDetail.builder()
                    .label(dimension)
                    .value(score)
                    .level(level)
                    .range(range)
                    .interpretation(interpretation)
                    .build();

            details.add(detail);
        }

        return details;
    }

    /**
     * 获取维度解释（支持维度特定的阈值和解释）
     */
    private String getDimensionInterpretation(String dimension, String level, int score, int[] thresholds) {
        // 根据不同维度提供专业的解释
        switch (dimension) {
            case "害怕否定评价":
                return getInterpretationForFearOfNegativeEvaluation(level, score, thresholds);
            case "社交回避及苦恼":
                return getInterpretationForSocialAvoidance(level, score, thresholds);
            default:
                return getDefaultDimensionInterpretation(dimension, level, score, thresholds);
        }
    }

    /**
     * 害怕否定评价维度的解释
     */
    private String getInterpretationForFearOfNegativeEvaluation(String level, int score, int[] thresholds) {
        if ("低风险".equals(level)) {
            return String.format("得分为%d分（≤%d分），表明孩子对他人评价的担心程度在正常范围内，能够较好地应对社交场合中的评价压力。",
                    score, thresholds[0]);
        } else if ("中度风险".equals(level)) {
            return String.format("得分为%d分（%d-%d分），表明孩子对他人否定评价存在一定程度的担心，在社交场合可能会感到紧张，建议给予适当的支持和鼓励。",
                    score, thresholds[0] + 1, thresholds[1]);
        } else {
            return String.format("得分为%d分（>%d分），表明孩子对他人否定评价存在较强的担心，可能会显著影响其社交参与，建议寻求专业指导。",
                    score, thresholds[1]);
        }
    }

    /**
     * 社交回避及苦恼维度的解释
     */
    private String getInterpretationForSocialAvoidance(String level, int score, int[] thresholds) {
        if ("低风险".equals(level)) {
            return String.format("得分为%d分（≤%d分），表明孩子在社交场合表现自然，较少出现回避行为，社交适应良好。",
                    score, thresholds[0]);
        } else if ("中度风险".equals(level)) {
            return String.format("得分为%d分（%d-%d分），表明孩子在某些社交场合可能会感到不适或选择回避，建议逐步引导其参与社交活动。",
                    score, thresholds[0] + 1, thresholds[1]);
        } else {
            return String.format("得分为%d分（>%d分），表明孩子存在明显的社交回避行为，可能会影响其社交技能发展，建议重点关注并寻求专业帮助。",
                    score, thresholds[1]);
        }
    }

    /**
     * 默认维度解释
     */
    private String getDefaultDimensionInterpretation(String dimension, String level, int score, int[] thresholds) {
        if ("低风险".equals(level)) {
            return String.format("%s得分为%d分，在正常范围内，表现良好。", dimension, score);
        } else if ("中度风险".equals(level)) {
            return String.format("%s得分为%d分，存在一定程度的困难，建议给予关注和支持。", dimension, score);
        } else {
            return String.format("%s得分为%d分，存在较明显的困难，建议寻求专业指导。", dimension, score);
        }
    }

    /**
     * 获取总分的阈值范围
     */
    private int[] getSummaryRange(String level) {
        if ("低风险".equals(level)) {
            return new int[]{0, 8};  // 0-8分
        } else if ("中度风险".equals(level)) {
            return new int[]{9, 16};  // 9-16分
        } else {
            return new int[]{17, 36};  // 17-36分
        }
    }

    /**
     * 获取总分的描述
     */
    private String getSummaryDescription(String level) {
        if ("低风险".equals(level)) {
            return "孩子的社交焦虑水平在正常范围内，社交适应能力良好。";
        } else if ("中度风险".equals(level)) {
            return "孩子存在一定程度的社交焦虑，需要适当关注和引导。";
        } else {
            return "孩子的社交焦虑水平较高，建议寻求专业帮助和干预。";
        }
    }

    /**
     * 生成总分的建议
     */
    private QuestionnaireResultDTO.Advice generateSummaryAdvice(String level) {
        List<String> content = new ArrayList<>();
        String description;

        if ("低风险".equals(level)) {
            description = "维护良好社交能力的建议";
            content.add("继续保持良好的亲子沟通，倾听孩子的想法和感受");
            content.add("鼓励孩子参与适合的社交活动，培养社交技能");
            content.add("给予孩子充分的肯定和支持，增强其自信心");
            content.add("定期关注孩子的情绪变化，及时给予关爱");
            content.add("创造积极正面的社交环境");
        } else if ("中度风险".equals(level)) {
            description = "缓解社交焦虑的建议";
            content.add("创造温馨的家庭环境，让孩子感受到安全感");
            content.add("逐步引导孩子参与小规模的社交活动");
            content.add("教授孩子一些应对焦虑的简单技巧，如深呼吸");
            content.add("避免过度保护，适当鼓励孩子面对挑战");
            content.add("如情况持续，可考虑寻求专业心理咨询师的帮助");
        } else {
            description = "专业干预和支持的建议";
            content.add("立即寻求专业儿童心理健康专家的评估和指导");
            content.add("与学校老师密切沟通，了解孩子在校表现");
            content.add("考虑专业的心理干预或治疗方案");
            content.add("给予孩子更多的耐心、理解和无条件的爱");
            content.add("避免批评或强迫孩子参与社交活动");
            content.add("建立支持性的家庭和学校环境");
        }

        return QuestionnaireResultDTO.Advice.builder()
                .description(description)
                .content(content)
                .build();
    }

    /**
     * 获取维度的阈值范围
     */
    private int[] getDimensionRange(String dimension, String level, int[] thresholds) {
        if ("低风险".equals(level)) {
            return new int[]{0, thresholds[0]};
        } else if ("中度风险".equals(level)) {
            return new int[]{thresholds[0] + 1, thresholds[1]};
        } else {
            // 高风险，需要根据维度确定最大值
            int maxScore = getDimensionMaxScore(dimension);
            return new int[]{thresholds[1] + 1, maxScore};
        }
    }

    /**
     * 获取维度的最大分数
     */
    private int getDimensionMaxScore(String dimension) {
        switch (dimension) {
            case "害怕否定评价":
                return 12;  // 6题 × 2分
            case "社交回避及苦恼":
                return 8;   // 4题 × 2分
            default:
                return 20;  // 默认最大值
        }
    }

}

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
import java.util.Set;
import java.util.HashSet;

/**
 * 儿童抑郁障碍自评量表（DSRSC）结果生成器
 * 问卷ID: 15
 *
 * @author 芋道源码
 */
@Component
public class ChildDepDisorderQuestionnaireGenerator extends AbstractQuestionnaireResultGenerator {

    private static final Long QUESTIONNAIRE_ID = 15L;
    private static final String QUESTIONNAIRE_NAME = "儿童抑郁障碍自评量表（DSRSC）";

    // 评分规则配置
    private static final Map<String, Integer> ANSWER_SCORES = new HashMap<>();
    static {
        ANSWER_SCORES.put("没有", 0);
        ANSWER_SCORES.put("有时有", 1);
        ANSWER_SCORES.put("经常有", 2);
    }

    // 反向计分题目（第1、2、4、7、8、9、11、12、13、16题）
    private static final Set<Integer> REVERSE_SCORE_QUESTIONS = new HashSet<>();
    static {
        REVERSE_SCORE_QUESTIONS.add(1);
        REVERSE_SCORE_QUESTIONS.add(2);
        REVERSE_SCORE_QUESTIONS.add(4);
        REVERSE_SCORE_QUESTIONS.add(7);
        REVERSE_SCORE_QUESTIONS.add(8);
        REVERSE_SCORE_QUESTIONS.add(9);
        REVERSE_SCORE_QUESTIONS.add(11);
        REVERSE_SCORE_QUESTIONS.add(12);
        REVERSE_SCORE_QUESTIONS.add(13);
        REVERSE_SCORE_QUESTIONS.add(16);
    }

    // 评分标准：>15分表示有抑郁的可能
    private static final int DEPRESSION_THRESHOLD = 15;

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
        // 1. 计算各题目分数（考虑反向计分）
        List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers = calculateAnswerScores(answerDTO.getAnswers());

        // 2. 计算总分
        int totalScore = calculateTotalScore(scoredAnswers);

        // 3. 确定评级和风险等级
        String level = getDepressionLevel(totalScore);

        // 4. 生成结果摘要
        QuestionnaireResultDTO.ResultSummary summary = generateSummary(totalScore, level);

        // 5. 生成详细结果（单一维度，无多因子）
        List<QuestionnaireResultDTO.ResultDetail> details = generateDetails(totalScore, level, scoredAnswers);

        return QuestionnaireResultDTO.builder()
                .score(BigDecimal.valueOf(totalScore))
                .level(level)
                .summary(summary)
                .details(details)
                .build();
    }

    /**
     * 计算答案分数（处理正向和反向计分）
     */
    private List<QuestionnaireAnswerDTO.AnswerItem> calculateAnswerScores(List<QuestionnaireAnswerDTO.AnswerItem> answers) {
        List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers = new ArrayList<>();

        for (QuestionnaireAnswerDTO.AnswerItem answer : answers) {
            Integer baseScore = ANSWER_SCORES.get(answer.getAnswer());
            if (baseScore == null) {
                baseScore = 0; // 默认分数
            }

            // 检查是否为反向计分题目
            int finalScore;
            if (REVERSE_SCORE_QUESTIONS.contains(answer.getIndex())) {
                // 反向计分：没有=2分、有时有=1分、经常有=0分
                finalScore = 2 - baseScore;
            } else {
                // 正向计分：没有=0分、有时有=1分、经常有=2分
                finalScore = baseScore;
            }

            QuestionnaireAnswerDTO.AnswerItem scoredAnswer = QuestionnaireAnswerDTO.AnswerItem.builder()
                    .title(answer.getTitle())
                    .answer(answer.getAnswer())
                    .index(answer.getIndex())
                    .score(finalScore)
                    .build();

            scoredAnswers.add(scoredAnswer);
        }

        return scoredAnswers;
    }

    /**
     * 根据总分确定抑郁程度等级
     */
    private String getDepressionLevel(int totalScore) {
        if (totalScore <= DEPRESSION_THRESHOLD) {
            return "正常范围";
        } else {
            return "可能抑郁";
        }
    }

    /**
     * 生成结果摘要
     */
    private QuestionnaireResultDTO.ResultSummary generateSummary(int totalScore, String level) {
        String interpretation = getDepressionInterpretation(totalScore, level);
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
     * 获取总分的阈值范围
     */
    private int[] getSummaryRange(String level) {
        if ("正常范围".equals(level)) {
            return new int[]{0, DEPRESSION_THRESHOLD};  // 0-15分
        } else {
            return new int[]{DEPRESSION_THRESHOLD + 1, 36};  // 16-36分
        }
    }

    /**
     * 获取总分的描述
     */
    private String getSummaryDescription(String level) {
        if ("正常范围".equals(level)) {
            return "孩子的抑郁症状在正常范围内，心理状态相对健康。";
        } else {
            return "孩子可能存在抑郁症状，需要引起重视并采取相应措施。";
        }
    }

    /**
     * 生成总分的建议
     */
    private QuestionnaireResultDTO.Advice generateSummaryAdvice(String level) {
        List<String> content = new ArrayList<>();
        String description;

        if ("正常范围".equals(level)) {
            description = "维护孩子心理健康的建议";
            content.add("继续保持规律的作息和健康的生活习惯");
            content.add("鼓励孩子参与喜欢的活动和运动");
            content.add("维持良好的亲子关系和同伴关系");
            content.add("定期关注孩子的情绪变化");
            content.add("创造积极正面的家庭环境");
        } else {
            description = "需要重点关注和干预的建议";
            content.add("立即寻求专业儿童心理健康专家的评估");
            content.add("增加对孩子的陪伴和情感支持");
            content.add("与学校老师密切沟通，了解在校表现");
            content.add("考虑专业的心理治疗或药物治疗");
            content.add("建立安全的家庭环境，移除可能的危险物品");
            content.add("如发现自伤或自杀倾向，立即寻求紧急医疗帮助");
        }

        return QuestionnaireResultDTO.Advice.builder()
                .description(description)
                .content(content)
                .build();
    }

    /**
     * 获取抑郁程度解释
     */
    private String getDepressionInterpretation(int totalScore, String level) {
        if ("正常范围".equals(level)) {
            return String.format("总分为%d分（≤%d分），在正常范围内，未发现明显的抑郁症状，心理状态良好。",
                    totalScore, DEPRESSION_THRESHOLD);
        } else {
            return String.format("总分为%d分（>%d分），超过临界值，提示可能存在抑郁症状，建议进一步关注孩子的心理状态，必要时寻求专业帮助。",
                    totalScore, DEPRESSION_THRESHOLD);
        }
    }

    /**
     * 生成详细结果（单一维度分析）
     */
    private List<QuestionnaireResultDTO.ResultDetail> generateDetails(int totalScore, String level,
                                                                    List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers) {
        List<QuestionnaireResultDTO.ResultDetail> details = new ArrayList<>();

        // 分析正向计分题目（抑郁症状相关）
        int positiveScore = calculatePositiveScore(scoredAnswers);
        details.add(createPositiveSymptomDetail(positiveScore));

        // 分析反向计分题目（积极情绪相关）
        int reverseScore = calculateReverseScore(scoredAnswers);
        details.add(createPositiveEmotionDetail(reverseScore));

        // 整体风险评估
        details.add(createOverallRiskDetail(totalScore, level));

        return details;
    }

    /**
     * 计算正向计分题目得分（抑郁症状）
     */
    private int calculatePositiveScore(List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers) {
        return scoredAnswers.stream()
                .filter(answer -> !REVERSE_SCORE_QUESTIONS.contains(answer.getIndex()))
                .mapToInt(answer -> answer.getScore() != null ? answer.getScore() : 0)
                .sum();
    }

    /**
     * 计算反向计分题目得分（积极情绪）
     */
    private int calculateReverseScore(List<QuestionnaireAnswerDTO.AnswerItem> scoredAnswers) {
        return scoredAnswers.stream()
                .filter(answer -> REVERSE_SCORE_QUESTIONS.contains(answer.getIndex()))
                .mapToInt(answer -> answer.getScore() != null ? answer.getScore() : 0)
                .sum();
    }

    /**
     * 创建抑郁症状详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createPositiveSymptomDetail(int positiveScore) {
        String level;
        String interpretation;
        int[] range;

        // 正向计分题目共8题，满分16分
        if (positiveScore <= 5) {
            level = "轻微";
            range = new int[]{0, 5};
            interpretation = String.format("抑郁症状得分为%d分，症状较轻微，偶尔出现负面情绪属于正常现象。", positiveScore);
        } else if (positiveScore <= 10) {
            level = "中等";
            range = new int[]{6, 10};
            interpretation = String.format("抑郁症状得分为%d分，存在一定程度的抑郁症状，建议关注孩子的情绪变化。", positiveScore);
        } else {
            level = "明显";
            range = new int[]{11, 16};
            interpretation = String.format("抑郁症状得分为%d分，抑郁症状较为明显，建议及时寻求专业帮助。", positiveScore);
        }

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("抑郁症状表现")
                .value(positiveScore)
                .level(level)
                .range(range)
                .interpretation(interpretation)
                .build();
    }

    /**
     * 创建积极情绪详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createPositiveEmotionDetail(int reverseScore) {
        String level;
        String interpretation;
        int[] range;

        // 反向计分题目共10题，满分20分
        if (reverseScore >= 15) {
            level = "良好";
            range = new int[]{15, 20};
            interpretation = String.format("积极情绪得分为%d分，孩子能够体验到较多的积极情绪，心理韧性较好。", reverseScore);
        } else if (reverseScore >= 10) {
            level = "一般";
            range = new int[]{10, 14};
            interpretation = String.format("积极情绪得分为%d分，孩子的积极情绪体验一般，可以通过一些活动来提升。", reverseScore);
        } else {
            level = "不足";
            range = new int[]{0, 9};
            interpretation = String.format("积极情绪得分为%d分，孩子缺乏积极情绪体验，建议增加愉快活动，培养兴趣爱好。", reverseScore);
        }

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("积极情绪体验")
                .value(reverseScore)
                .level(level)
                .range(range)
                .interpretation(interpretation)
                .build();
    }

    /**
     * 创建整体风险评估详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createOverallRiskDetail(int totalScore, String level) {
        String riskLevel;
        String interpretation;
        int[] range;

        if ("正常范围".equals(level)) {
            riskLevel = "低风险";
            range = new int[]{0, DEPRESSION_THRESHOLD};
            interpretation = String.format("综合评估显示总分为%d分，在正常范围内，孩子的心理状态健康，无明显抑郁风险。建议继续保持良好的生活习惯和积极的心态。", totalScore);
        } else {
            riskLevel = "需关注";
            range = new int[]{DEPRESSION_THRESHOLD + 1, 36};
            interpretation = String.format("综合评估显示总分为%d分，超过临界值，提示存在抑郁风险。建议：1）密切关注孩子的情绪变化；2）增加亲子沟通时间；3）必要时寻求专业心理咨询。", totalScore);
        }

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("整体风险评估")
                .value(totalScore)
                .level(riskLevel)
                .range(range)
                .interpretation(interpretation)
                .build();
    }

}

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
 * 儿童精细动作测评量表结果生成器
 * 问卷ID: 17
 *
 * @author 芋道源码
 */
@Component
public class ChildFineMotorQuestionnaireGenerator extends AbstractQuestionnaireResultGenerator {

    private static final Long QUESTIONNAIRE_ID = 17L;
    private static final String QUESTIONNAIRE_NAME = "儿童精细动作测评量表";

    // 评分规则配置
    private static final Map<String, Integer> ANSWER_SCORES = new HashMap<>();
    static {
        ANSWER_SCORES.put("能做到", 3);
        ANSWER_SCORES.put("不能做到", 0);
    }

    // 维度配置（假设20题，分为4个维度，每个维度5题）
    private static final Map<String, int[]> DIMENSION_QUESTIONS = new HashMap<>();
    static {
        DIMENSION_QUESTIONS.put("手指精细操作", new int[]{1, 2, 3, 4, 5});
        DIMENSION_QUESTIONS.put("手眼协调", new int[]{6, 7, 8, 9, 10});
        DIMENSION_QUESTIONS.put("双手协调", new int[]{11, 12, 13, 14, 15});
        DIMENSION_QUESTIONS.put("工具使用", new int[]{16, 17, 18, 19, 20});
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
        String level = getFineMotorLevel(totalScore);

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
     * 根据总分确定精细动作发展水平
     */
    private String getFineMotorLevel(int totalScore) {
        // 总分范围：0-60分（20题 × 3分）
        if (totalScore >= 50) {
            return "优秀";
        } else if (totalScore >= 40) {
            return "良好";
        } else if (totalScore >= 30) {
            return "一般";
        } else {
            return "需要提升";
        }
    }

    /**
     * 生成结果摘要
     */
    private QuestionnaireResultDTO.ResultSummary generateSummary(int totalScore, String level) {
        String interpretation = getFineMotorInterpretation(totalScore, level);
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
     * 获取精细动作发展解释
     */
    private String getFineMotorInterpretation(int totalScore, String level) {
        switch (level) {
            case "优秀":
                return String.format("总分为%d分（≥50分），精细动作发展优秀，各项技能掌握良好，手部协调能力强。", totalScore);
            case "良好":
                return String.format("总分为%d分（40-49分），精细动作发展良好，大部分技能掌握较好，个别方面可进一步提升。", totalScore);
            case "一般":
                return String.format("总分为%d分（30-39分），精细动作发展一般，部分技能需要加强练习和指导。", totalScore);
            default:
                return String.format("总分为%d分（<30分），精细动作发展需要提升，建议加强相关训练和专业指导。", totalScore);
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

        // 整体发展评估
        details.add(createOverallDevelopmentDetail(totalScore, level));

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
     * 创建整体发展评估详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createOverallDevelopmentDetail(int totalScore, String level) {
        int[] range = getSummaryRange(level);
        String interpretation = getOverallDevelopmentInterpretation(totalScore, level);

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("整体发展评估")
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
                return String.format("%s得分为%d分（满分%d分），表现优秀，该领域技能掌握良好。", dimension, score, maxScore);
            case "良好":
                return String.format("%s得分为%d分（满分%d分），表现良好，大部分技能掌握较好。", dimension, score, maxScore);
            case "一般":
                return String.format("%s得分为%d分（满分%d分），表现一般，部分技能需要加强练习。", dimension, score, maxScore);
            default:
                return String.format("%s得分为%d分（满分%d分），需要提升，建议重点关注该领域的训练。", dimension, score, maxScore);
        }
    }

    /**
     * 获取整体发展解释
     */
    private String getOverallDevelopmentInterpretation(int totalScore, String level) {
        switch (level) {
            case "优秀":
                return String.format("综合评估显示总分为%d分，精细动作发展优秀，各项技能均达到良好水平，建议继续保持。", totalScore);
            case "良好":
                return String.format("综合评估显示总分为%d分，精细动作发展良好，可通过针对性练习进一步提升。", totalScore);
            case "一般":
                return String.format("综合评估显示总分为%d分，精细动作发展一般，建议加强日常练习和专业指导。", totalScore);
            default:
                return String.format("综合评估显示总分为%d分，精细动作发展需要提升，建议寻求专业的康复训练指导。", totalScore);
        }
    }

    /**
     * 获取总分的阈值范围
     */
    private int[] getSummaryRange(String level) {
        switch (level) {
            case "优秀":
                return new int[]{50, 60};
            case "良好":
                return new int[]{40, 49};
            case "一般":
                return new int[]{30, 39};
            default:
                return new int[]{0, 29};
        }
    }

    /**
     * 获取总分的描述
     */
    private String getSummaryDescription(String level) {
        switch (level) {
            case "优秀":
                return "孩子的精细动作发展优秀，手部协调能力强，各项技能掌握良好。";
            case "良好":
                return "孩子的精细动作发展良好，大部分技能掌握较好，个别方面可进一步提升。";
            case "一般":
                return "孩子的精细动作发展一般，部分技能需要加强练习和指导。";
            default:
                return "孩子的精细动作发展需要提升，建议加强相关训练和专业指导。";
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
                description = "维护和发展精细动作技能的建议";
                content.add("继续提供丰富的精细动作练习机会");
                content.add("尝试更具挑战性的手工活动和游戏");
                content.add("鼓励孩子学习新的技能，如乐器演奏、绘画等");
                content.add("定期评估发展状况，保持技能水平");
                content.add("可以帮助其他孩子，增强自信心");
                break;
            case "良好":
                description = "进一步提升精细动作技能的建议";
                content.add("增加日常精细动作练习的频率和多样性");
                content.add("重点关注得分较低的维度进行针对性训练");
                content.add("提供适合年龄的手工制作和拼装玩具");
                content.add("鼓励参与绘画、书写等精细动作活动");
                content.add("与老师配合，在学校也进行相关练习");
                break;
            case "一般":
                description = "加强精细动作技能训练的建议";
                content.add("制定系统的精细动作训练计划");
                content.add("从简单的抓握、捏取动作开始练习");
                content.add("使用专业的精细动作训练器材和玩具");
                content.add("寻求职业治疗师的专业指导");
                content.add("每天安排固定时间进行精细动作练习");
                break;
            default:
                description = "专业干预和康复训练的建议";
                content.add("立即寻求儿童康复专家或职业治疗师的评估");
                content.add("制定个性化的康复训练方案");
                content.add("考虑专业的精细动作康复训练课程");
                content.add("家庭和学校密切配合，共同实施训练计划");
                content.add("定期监测进展，调整训练方案");
                content.add("必要时进行医学检查，排除器质性问题");
                break;
        }

        return QuestionnaireResultDTO.Advice.builder()
                .description(description)
                .content(content)
                .build();
    }

}

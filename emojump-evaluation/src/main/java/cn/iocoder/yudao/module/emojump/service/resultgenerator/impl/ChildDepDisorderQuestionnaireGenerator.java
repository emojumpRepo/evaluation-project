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

        return QuestionnaireResultDTO.ResultSummary.builder()
                .label("总分")
                .value(totalScore)
                .level(level)
                .interpretation(interpretation)
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

        // 正向计分题目共8题，满分16分
        if (positiveScore <= 5) {
            level = "轻微";
            interpretation = String.format("抑郁症状得分为%d分，症状较轻微，偶尔出现负面情绪属于正常现象。", positiveScore);
        } else if (positiveScore <= 10) {
            level = "中等";
            interpretation = String.format("抑郁症状得分为%d分，存在一定程度的抑郁症状，建议关注孩子的情绪变化。", positiveScore);
        } else {
            level = "明显";
            interpretation = String.format("抑郁症状得分为%d分，抑郁症状较为明显，建议及时寻求专业帮助。", positiveScore);
        }

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("抑郁症状表现")
                .value(positiveScore)
                .level(level)
                .interpretation(interpretation)
                .build();
    }

    /**
     * 创建积极情绪详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createPositiveEmotionDetail(int reverseScore) {
        String level;
        String interpretation;

        // 反向计分题目共10题，满分20分
        if (reverseScore >= 15) {
            level = "良好";
            interpretation = String.format("积极情绪得分为%d分，孩子能够体验到较多的积极情绪，心理韧性较好。", reverseScore);
        } else if (reverseScore >= 10) {
            level = "一般";
            interpretation = String.format("积极情绪得分为%d分，孩子的积极情绪体验一般，可以通过一些活动来提升。", reverseScore);
        } else {
            level = "不足";
            interpretation = String.format("积极情绪得分为%d分，孩子缺乏积极情绪体验，建议增加愉快活动，培养兴趣爱好。", reverseScore);
        }

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("积极情绪体验")
                .value(reverseScore)
                .level(level)
                .interpretation(interpretation)
                .build();
    }

    /**
     * 创建整体风险评估详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createOverallRiskDetail(int totalScore, String level) {
        String riskLevel;
        String interpretation;

        if ("正常范围".equals(level)) {
            riskLevel = "低风险";
            interpretation = String.format("综合评估显示总分为%d分，在正常范围内，孩子的心理状态健康，无明显抑郁风险。建议继续保持良好的生活习惯和积极的心态。", totalScore);
        } else {
            riskLevel = "需关注";
            interpretation = String.format("综合评估显示总分为%d分，超过临界值，提示存在抑郁风险。建议：1）密切关注孩子的情绪变化；2）增加亲子沟通时间；3）必要时寻求专业心理咨询。", totalScore);
        }

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("整体风险评估")
                .value(totalScore)
                .level(riskLevel)
                .interpretation(interpretation)
                .build();
    }

    @Override
    protected String generateReportHtml(QuestionnaireResultDTO result, QuestionnaireAnswerDTO answerDTO) {
        StringBuilder report = new StringBuilder();

        // 报告标题
        report.append("<div style='text-align: center; margin-bottom: 20px;'>");
        report.append("<h1 style='color: #2c3e50; font-size: 24px; margin: 0;'>🧠 儿童抑郁障碍自评报告</h1>");
        report.append("</div>");

        // 评估时间
        report.append("<div style='text-align: center; color: #7f8c8d; margin-bottom: 30px;'>");
        report.append("🕐 评估时间：")
              .append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")));
        report.append("</div>");

        // 总体评估结果
        String bgColor = "正常范围".equals(result.getSummary().getLevel()) ?
                "linear-gradient(135deg, #27ae60 0%, #2ecc71 100%)" :
                "linear-gradient(135deg, #e74c3c 0%, #c0392b 100%)";

        report.append("<div style='background: ").append(bgColor).append("; color: white; padding: 20px; border-radius: 10px; margin: 20px 0;'>");
        report.append("<h2 style='color: white; margin: 0 0 15px 0; font-size: 20px;'>📊 总体评估结果</h2>");

        report.append("<div style='margin-bottom: 10px;'>");
        report.append("<span style='font-size: 18px; font-weight: bold;'>🎯 总分：").append(result.getSummary().getValue()).append(" 分</span>");
        report.append("</div>");

        report.append("<div style='margin-bottom: 15px;'>");
        report.append("<span style='font-size: 18px; font-weight: bold;'>📈 评级：").append(getLevelEmoji(result.getSummary().getLevel()))
              .append(" <span style='background: rgba(255,255,255,0.2); padding: 5px 10px; border-radius: 15px;'>").append(result.getSummary().getLevel()).append("</span></span>");
        report.append("</div>");

        report.append("<div style='line-height: 1.6;'>");
        report.append("<strong>💡 结果解读：</strong><br>");
        report.append(result.getSummary().getInterpretation());
        report.append("</div>");
        report.append("</div>");

        // 各维度详细结果
        report.append("<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin: 30px 0 20px 0;'>🔍 详细分析</h2>");

        for (int i = 0; i < result.getDetails().size(); i++) {
            QuestionnaireResultDTO.ResultDetail detail = result.getDetails().get(i);

            report.append("<div style='margin: 20px 0; padding: 15px; border-left: 4px solid ").append(getLevelColor(detail.getLevel())).append("; background: #f8f9fa; border-radius: 0 8px 8px 0;'>");

            report.append("<h3 style='color: #2c3e50; margin: 0 0 10px 0; font-size: 18px;'>【").append(i + 1).append("】").append(detail.getLabel()).append("</h3>");

            report.append("<div style='margin-bottom: 8px;'>");
            report.append("<span style='font-weight: bold; color: #3498db;'>得分：").append(detail.getValue()).append(" 分</span>");
            report.append(" | ");
            report.append("<span style='font-weight: bold; color: ").append(getLevelColor(detail.getLevel())).append(";'>评级：").append(getLevelEmoji(detail.getLevel()))
                  .append(" ").append(detail.getLevel()).append("</span>");
            report.append("</div>");

            report.append("<div style='color: #555; line-height: 1.5;'>");
            report.append("<strong>解读：</strong>").append(detail.getInterpretation());
            report.append("</div>");

            report.append("</div>");
        }

        // 专业建议
        report.append("<div style='background: #e8f4fd; padding: 20px; border-radius: 10px; margin: 30px 0; border-left: 5px solid #3498db;'>");
        report.append("<h2 style='color: #3498db; margin: 0 0 15px 0; font-size: 20px;'>💡 专业建议</h2>");
        report.append("<div style='line-height: 1.6; color: #2c3e50;'>");
        report.append(generateProfessionalAdvice(result));
        report.append("</div>");
        report.append("</div>");

        // 注意事项
        report.append("<div style='background: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 5px solid #ffc107;'>");
        report.append("<h3 style='color: #856404; margin: 0 0 10px 0;'>⚠️ 重要提醒</h3>");
        report.append("<ul style='margin: 0; padding-left: 20px; color: #856404; line-height: 1.5;'>");
        report.append("<li>本评估结果仅供参考，不能替代专业医学诊断</li>");
        report.append("<li>如果孩子总分>15分，建议及时咨询专业的儿童心理健康专家</li>");
        report.append("<li>儿童心理发展存在个体差异，请结合实际情况综合判断</li>");
        report.append("<li>如发现孩子有自伤或自杀倾向，请立即寻求紧急医疗帮助</li>");
        report.append("</ul>");
        report.append("</div>");

        // 报告结尾
        report.append("<div style='text-align: center; margin-top: 30px; padding: 20px; background: #f8f9fa; border-radius: 8px;'>");
        report.append("<div style='color: #7f8c8d; margin-bottom: 10px;'>📞 如需专业帮助，请联系儿童心理健康机构</div>");
        report.append("<div style='color: #3498db; font-weight: bold;'>🌈 关爱孩子心理健康，共同守护美好童年！</div>");
        report.append("</div>");

        return report.toString();
    }

    /**
     * 获取评级对应的表情符号
     */
    private String getLevelEmoji(String level) {
        if (level.contains("正常") || level.contains("良好") || level.contains("低风险")) {
            return "✅";
        } else if (level.contains("一般") || level.contains("中等") || level.contains("轻微")) {
            return "⚠️";
        } else {
            return "🚨";
        }
    }

    /**
     * 获取评级对应的颜色
     */
    private String getLevelColor(String level) {
        if (level.contains("正常") || level.contains("良好") || level.contains("低风险")) {
            return "#27ae60";  // 绿色
        } else if (level.contains("一般") || level.contains("中等") || level.contains("轻微")) {
            return "#f39c12";  // 橙色
        } else {
            return "#e74c3c";  // 红色
        }
    }

    /**
     * 生成专业建议
     */
    private String generateProfessionalAdvice(QuestionnaireResultDTO result) {
        StringBuilder advice = new StringBuilder();

        String level = result.getSummary().getLevel();
        int totalScore = result.getSummary().getValue();

        if ("正常范围".equals(level)) {
            advice.append("<p style='margin: 10px 0;'><strong>🎉 评估结果良好！</strong></p>");
            advice.append("<p style='margin: 10px 0;'><strong>维护心理健康的建议：</strong></p>");
            advice.append("<ul style='margin: 10px 0; padding-left: 20px;'>");
            advice.append("<li style='margin: 5px 0;'>继续保持规律的作息和健康的生活习惯</li>");
            advice.append("<li style='margin: 5px 0;'>鼓励孩子参与喜欢的活动和运动</li>");
            advice.append("<li style='margin: 5px 0;'>维持良好的亲子关系和同伴关系</li>");
            advice.append("<li style='margin: 5px 0;'>定期关注孩子的情绪变化</li>");
            advice.append("</ul>");

        } else {
            advice.append("<p style='margin: 10px 0;'><strong>🚨 需要重点关注！</strong></p>");
            advice.append("<p style='margin: 10px 0;'><strong>紧急干预建议：</strong></p>");
            advice.append("<ul style='margin: 10px 0; padding-left: 20px;'>");
            advice.append("<li style='margin: 5px 0;'>立即寻求专业儿童心理健康专家的评估</li>");
            advice.append("<li style='margin: 5px 0;'>增加对孩子的陪伴和情感支持</li>");
            advice.append("<li style='margin: 5px 0;'>与学校老师密切沟通，了解在校表现</li>");
            advice.append("<li style='margin: 5px 0;'>考虑专业的心理治疗或药物治疗</li>");
            advice.append("<li style='margin: 5px 0;'>建立安全的家庭环境，移除可能的危险物品</li>");
            advice.append("</ul>");

            advice.append("<p style='margin: 15px 0; padding: 10px; background: #ffebee; border-left: 4px solid #f44336; color: #c62828;'>");
            advice.append("<strong>⚠️ 特别提醒：</strong>如果孩子表现出自伤、自杀想法或行为，请立即拨打心理危机干预热线或前往医院急诊科！");
            advice.append("</p>");
        }

        // 通用建议
        advice.append("<p style='margin: 15px 0;'><strong>日常关爱要点：</strong></p>");
        advice.append("<ul style='margin: 10px 0; padding-left: 20px;'>");
        advice.append("<li style='margin: 5px 0;'>倾听孩子的想法和感受，不要急于评判</li>");
        advice.append("<li style='margin: 5px 0;'>帮助孩子建立积极的应对策略</li>");
        advice.append("<li style='margin: 5px 0;'>鼓励孩子表达情感，而不是压抑</li>");
        advice.append("<li style='margin: 5px 0;'>适当减少学习压力，关注孩子的兴趣爱好</li>");
        advice.append("</ul>");

        return advice.toString();
    }
}

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

        return QuestionnaireResultDTO.ResultSummary.builder()
                .label("总分")
                .value(totalScore)
                .level(level)
                .interpretation(interpretation)
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

    @Override
    protected String generateReportHtml(QuestionnaireResultDTO result, QuestionnaireAnswerDTO answerDTO) {
        StringBuilder report = new StringBuilder();

        // 报告标题
        report.append("<div style='text-align: center; margin-bottom: 20px;'>");
        report.append("<h1 style='color: #2c3e50; font-size: 24px; margin: 0;'>📊 儿童社交焦虑评估报告</h1>");
        report.append("</div>");

        // 评估时间
        report.append("<div style='text-align: center; color: #7f8c8d; margin-bottom: 30px;'>");
        report.append("🕐 评估时间：")
              .append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")));
        report.append("</div>");

        // 总体评估结果
        report.append("<div style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin: 20px 0;'>");
        report.append("<h2 style='color: white; margin: 0 0 15px 0; font-size: 20px;'>📈 总体评估结果</h2>");

        report.append("<div style='margin-bottom: 10px;'>");
        report.append("<span style='font-size: 18px; font-weight: bold;'>🎯 总分：").append(result.getSummary().getValue()).append(" 分</span>");
        report.append("</div>");

        report.append("<div style='margin-bottom: 15px;'>");
        report.append("<span style='font-size: 18px; font-weight: bold;'>📊 评级：").append(getLevelEmoji(result.getSummary().getLevel()))
              .append(" <span style='background: rgba(255,255,255,0.2); padding: 5px 10px; border-radius: 15px;'>").append(result.getSummary().getLevel()).append("</span></span>");
        report.append("</div>");

        report.append("<div style='line-height: 1.6;'>");
        report.append("<strong>💡 结果解读：</strong><br>");
        report.append(result.getSummary().getInterpretation());
        report.append("</div>");
        report.append("</div>");

        // 各维度详细结果
        report.append("<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin: 30px 0 20px 0;'>🔍 各维度详细分析</h2>");

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

        // 建议与指导
        report.append("<div style='background: #e8f5e8; padding: 20px; border-radius: 10px; margin: 30px 0; border-left: 5px solid #27ae60;'>");
        report.append("<h2 style='color: #27ae60; margin: 0 0 15px 0; font-size: 20px;'>💝 专业建议</h2>");
        report.append("<div style='line-height: 1.6; color: #2c3e50;'>");
        report.append(formatAdviceWithHtml(generateProfessionalAdvice(result)));
        report.append("</div>");
        report.append("</div>");

        // 注意事项
        report.append("<div style='background: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 5px solid #ffc107;'>");
        report.append("<h3 style='color: #856404; margin: 0 0 10px 0;'>⚠️ 重要提醒</h3>");
        report.append("<ul style='margin: 0; padding-left: 20px; color: #856404; line-height: 1.5;'>");
        report.append("<li>本评估结果仅供参考，不能替代专业医学诊断</li>");
        report.append("<li>如有疑虑，建议咨询专业的儿童心理健康专家</li>");
        report.append("<li>儿童发展存在个体差异，请结合实际情况综合判断</li>");
        report.append("</ul>");
        report.append("</div>");

        // 报告结尾
        report.append("<div style='text-align: center; margin-top: 30px; padding: 20px; background: #f8f9fa; border-radius: 8px;'>");
        report.append("<div style='color: #7f8c8d; margin-bottom: 10px;'>📞 如需进一步咨询，请联系专业机构</div>");
        report.append("<div style='color: #3498db; font-weight: bold;'>🌟 祝愿孩子健康快乐成长！</div>");
        report.append("</div>");

        return report.toString();
    }

    /**
     * 获取评级对应的表情符号
     */
    private String getLevelEmoji(String level) {
        if (level.contains("低风险") || level.contains("正常")) {
            return "✅";
        } else if (level.contains("中度风险") || level.contains("中等")) {
            return "⚠️";
        } else {
            return "🚨";
        }
    }

    /**
     * 获取评级对应的颜色
     */
    private String getLevelColor(String level) {
        if (level.contains("低风险") || level.contains("正常")) {
            return "#27ae60";  // 绿色
        } else if (level.contains("中度风险") || level.contains("中等")) {
            return "#f39c12";  // 橙色
        } else {
            return "#e74c3c";  // 红色
        }
    }

    /**
     * 将建议文本格式化为HTML
     */
    private String formatAdviceWithHtml(String advice) {
        // 将换行符转换为<br>标签
        String formatted = advice.replace("\n\n", "</p><p style='margin: 15px 0;'>")
                                 .replace("\n", "<br>");

        // 将列表项格式化
        formatted = formatted.replaceAll("• ([^<]+)", "<li style='margin: 5px 0;'>$1</li>");

        // 包装列表
        if (formatted.contains("<li")) {
            formatted = formatted.replaceAll("(<li[^>]*>[^<]+</li>)+", "<ul style='margin: 10px 0; padding-left: 20px;'>$0</ul>");
        }

        // 包装段落
        if (!formatted.startsWith("<p")) {
            formatted = "<p style='margin: 15px 0;'>" + formatted + "</p>";
        }

        return formatted;
    }

    /**
     * 生成专业建议
     */
    private String generateProfessionalAdvice(QuestionnaireResultDTO result) {
        StringBuilder advice = new StringBuilder();

        String level = result.getSummary().getLevel();
        int totalScore = result.getSummary().getValue();

        if ("低风险".equals(level)) {
            advice.append("🎉 您的孩子在社交焦虑方面表现良好！\n\n");
            advice.append("建议：\n");
            advice.append("• 继续保持良好的亲子沟通，倾听孩子的想法和感受\n");
            advice.append("• 鼓励孩子参与适合的社交活动，培养社交技能\n");
            advice.append("• 给予孩子充分的肯定和支持，增强其自信心\n");
            advice.append("• 定期关注孩子的情绪变化，及时给予关爱");

        } else if ("中度风险".equals(level)) {
            advice.append("🤔 您的孩子在社交方面存在一定程度的焦虑，需要适当关注。\n\n");
            advice.append("建议：\n");
            advice.append("• 创造温馨的家庭环境，让孩子感受到安全感\n");
            advice.append("• 逐步引导孩子参与小规模的社交活动\n");
            advice.append("• 教授孩子一些应对焦虑的简单技巧，如深呼吸\n");
            advice.append("• 避免过度保护，适当鼓励孩子面对挑战\n");
            advice.append("• 如情况持续，可考虑寻求专业心理咨询师的帮助");

        } else {
            advice.append("😟 您的孩子在社交焦虑方面需要重点关注和专业指导。\n\n");
            advice.append("建议：\n");
            advice.append("• 立即寻求专业儿童心理健康专家的评估和指导\n");
            advice.append("• 与学校老师密切沟通，了解孩子在校表现\n");
            advice.append("• 考虑专业的心理干预或治疗方案\n");
            advice.append("• 给予孩子更多的耐心、理解和无条件的爱\n");
            advice.append("• 避免批评或强迫孩子参与社交活动");
        }

        // 根据具体维度给出针对性建议
        for (QuestionnaireResultDTO.ResultDetail detail : result.getDetails()) {
            if ("高风险".equals(detail.getLevel())) {
                advice.append("\n\n针对【").append(detail.getLabel()).append("】的特别建议：\n");
                advice.append(getSpecificAdviceForDimension(detail.getLabel(), detail.getLevel()));
            }
        }

        return advice.toString();
    }

    /**
     * 获取特定维度的建议
     */
    private String getSpecificAdviceForDimension(String dimension, String level) {
        switch (dimension) {
            case "害怕否定评价":
                return "• 多给孩子正面鼓励，减少批评和比较\n" +
                       "• 教导孩子理解每个人都有优缺点，不必过分在意他人看法\n" +
                       "• 通过角色扮演等方式帮助孩子练习应对他人评价";

            case "社交回避及苦恼":
                return "• 从孩子感兴趣的活动开始，逐步扩大社交圈\n" +
                       "• 陪伴孩子参与社交活动，给予安全感\n" +
                       "• 教授基本的社交技巧，如问候、分享等";

            default:
                return "• 针对此维度的困难，建议咨询专业人士获取个性化指导";
        }
    }
}

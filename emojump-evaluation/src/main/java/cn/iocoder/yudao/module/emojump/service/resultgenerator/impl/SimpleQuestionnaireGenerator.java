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
        QuestionnaireResultDTO.ResultSummary summary = QuestionnaireResultDTO.ResultSummary.builder()
                .label("总分")
                .value(totalScore)
                .level(level)
                .interpretation(getSimpleInterpretation(level, totalScore))
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



    @Override
    protected String generateReportHtml(QuestionnaireResultDTO result, QuestionnaireAnswerDTO answerDTO) {
        StringBuilder report = new StringBuilder();

        // 报告标题
        report.append("<div style='text-align: center; margin-bottom: 20px;'>");
        report.append("<h1 style='color: #2c3e50; font-size: 24px; margin: 0;'>📋 ").append(QUESTIONNAIRE_NAME).append("</h1>");
        report.append("</div>");

        // 评估时间
        report.append("<div style='text-align: center; color: #7f8c8d; margin-bottom: 30px;'>");
        report.append("🕐 生成时间：")
              .append(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm")));
        report.append("</div>");

        // 总体评估结果
        report.append("<div style='background: linear-gradient(135deg, #3498db 0%, #2c3e50 100%); color: white; padding: 20px; border-radius: 10px; margin: 20px 0;'>");
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
        report.append("<h2 style='color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin: 30px 0 20px 0;'>📈 各维度详细分析</h2>");

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
        report.append("<h2 style='color: #27ae60; margin: 0 0 15px 0; font-size: 20px;'>💡 建议与指导</h2>");
        report.append("<div style='line-height: 1.6; color: #2c3e50;'>");

        String level = result.getSummary().getLevel();
        if ("低风险".equals(level)) {
            report.append("<p style='margin: 10px 0;'><strong>🎉 评估结果良好！</strong></p>");
            report.append("<p style='margin: 10px 0;'><strong>建议：</strong></p>");
            report.append("<ul style='margin: 10px 0; padding-left: 20px;'>");
            report.append("<li style='margin: 5px 0;'>继续保持当前的良好状态</li>");
            report.append("<li style='margin: 5px 0;'>定期进行自我评估和调整</li>");
            report.append("<li style='margin: 5px 0;'>适当挑战自己，促进进一步发展</li>");
            report.append("</ul>");
        } else if ("中度风险".equals(level)) {
            report.append("<p style='margin: 10px 0;'><strong>⚠️ 需要适当关注</strong></p>");
            report.append("<p style='margin: 10px 0;'><strong>建议：</strong></p>");
            report.append("<ul style='margin: 10px 0; padding-left: 20px;'>");
            report.append("<li style='margin: 5px 0;'>重点关注得分较低的维度</li>");
            report.append("<li style='margin: 5px 0;'>制定针对性的改进计划</li>");
            report.append("<li style='margin: 5px 0;'>寻求适当的支持和指导</li>");
            report.append("</ul>");
        } else {
            report.append("<p style='margin: 10px 0;'><strong>🚨 建议寻求专业帮助</strong></p>");
            report.append("<p style='margin: 10px 0;'><strong>建议：</strong></p>");
            report.append("<ul style='margin: 10px 0; padding-left: 20px;'>");
            report.append("<li style='margin: 5px 0;'>咨询相关领域的专业人士</li>");
            report.append("<li style='margin: 5px 0;'>制定系统性的干预方案</li>");
            report.append("<li style='margin: 5px 0;'>定期跟踪评估进展情况</li>");
            report.append("</ul>");
        }

        report.append("</div>");
        report.append("</div>");

        // 注意事项
        report.append("<div style='background: #fff3cd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 5px solid #ffc107;'>");
        report.append("<h3 style='color: #856404; margin: 0 0 10px 0;'>⚠️ 重要提醒</h3>");
        report.append("<ul style='margin: 0; padding-left: 20px; color: #856404; line-height: 1.5;'>");
        report.append("<li>本评估结果仅供参考，不能替代专业诊断</li>");
        report.append("<li>如有疑虑，建议咨询相关专业人士</li>");
        report.append("<li>个体差异较大，请结合实际情况综合判断</li>");
        report.append("</ul>");
        report.append("</div>");

        // 报告结尾
        report.append("<div style='text-align: center; margin-top: 30px; padding: 20px; background: #f8f9fa; border-radius: 8px;'>");
        report.append("<div style='color: #7f8c8d; margin-bottom: 10px;'>📞 如需进一步咨询，请联系专业机构</div>");
        report.append("<div style='color: #3498db; font-weight: bold;'>🌟 祝您健康快乐！</div>");
        report.append("</div>");

        return report.toString();
    }

    /**
     * 获取评级对应的表情符号
     */
    private String getLevelEmoji(String level) {
        if (level.contains("低风险") || level.contains("优秀") || level.contains("良好")) {
            return "✅";
        } else if (level.contains("中度风险") || level.contains("中等") || level.contains("正常")) {
            return "⚠️";
        } else {
            return "🚨";
        }
    }

    /**
     * 获取评级对应的颜色
     */
    private String getLevelColor(String level) {
        if (level.contains("低风险") || level.contains("优秀") || level.contains("良好")) {
            return "#27ae60";  // 绿色
        } else if (level.contains("中度风险") || level.contains("中等") || level.contains("正常")) {
            return "#f39c12";  // 橙色
        } else {
            return "#e74c3c";  // 红色
        }
    }


}

package cn.iocoder.yudao.module.emojump.service.resultgenerator.impl;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.assessment.AbstractAssessmentResultGenerator;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.assessment.AssessmentResultDTO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 默认测评结果生成器
 *
 * @author 芋道源码
 */
@Component
public class DefaultAssessmentResultGenerator extends AbstractAssessmentResultGenerator {

    private static final List<Long> SUPPORTED_ASSESSMENT_IDS = Arrays.asList(10L);
    private static final String ASSESSMENT_NAME = "儿童能力测评结果生成器";

    @Resource
    private MemberBabyService memberBabyService;

    @Override
    public List<Long> getSupportedAssessmentIds() {
        return SUPPORTED_ASSESSMENT_IDS;
    }

    @Override
    public String getAssessmentName() {
        return ASSESSMENT_NAME;
    }

    @Override
    protected AssessmentResultDTO calculateResult(Long assessmentId, Long babyId, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        // 计算总的智龄（五个相关问卷的所得分数相加，再除以5）
        double totalMentalAge = calculateTotalMentalAge(questionnaireResults);
        
        // 计算实际月龄
        double actualAge = calculateActualAge(babyId);
        
        // 计算发育商（将智龄除以实际月龄，再乘100）
        int developmentQuotient = calculateDevelopmentQuotient(totalMentalAge, actualAge);
        
        // 确定发育商评级
        String developmentLevel = determineDevelopmentLevel(developmentQuotient);
        
        // 生成结果摘要
        AssessmentResultDTO.ResultSummary summary = generateSummary(totalMentalAge, actualAge, developmentQuotient, developmentLevel);
        
        // 生成结果详情
        List<AssessmentResultDTO.ResultDetail> details = generateDetails(totalMentalAge, actualAge, developmentQuotient, developmentLevel, questionnaireResults);
        
        // 构建测评结果
        return AssessmentResultDTO.builder()
                .overallScore(BigDecimal.valueOf(developmentQuotient))
                .overallLevel(developmentLevel)
                .summary(summary)
                .details(details)
                .questionnaireResults(questionnaireResults)
                .report(generateReport(totalMentalAge, actualAge, developmentQuotient, developmentLevel, questionnaireResults))
                .build();
    }

    /**
     * 计算总的智龄
     * 将五个相关问卷的所得分数相加，再除以5，小数点后两位四舍五入，保留一位小数
     */
    private double calculateTotalMentalAge(List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        if (questionnaireResults == null || questionnaireResults.isEmpty()) {
            return 0.0;
        }
        
        double totalScore = questionnaireResults.stream()
                .mapToDouble(item -> item.getScore() != null ? item.getScore().doubleValue() : 0.0)
                .sum();
        
        double averageScore = totalScore / questionnaireResults.size();
        
        // 四舍五入到一位小数
        return Math.round(averageScore * 10.0) / 10.0;
    }

    /**
     * 计算实际月龄
     * 按照calculateMonthAge函数的逻辑计算月龄
     */
    private double calculateActualAge(Long babyId) {
        if (babyId == null) {
            return 0.0;
        }
        
        MemberBabyDO baby = memberBabyService.getBaby(babyId);
        if (baby == null || baby.getBirthday() == null) {
            return 0.0;
        }
        
        LocalDate birthDate = baby.getBirthday();
        LocalDate currentDate = LocalDate.now();
        
        // 计算年份差和月份差
        int monthAge = (currentDate.getYear() - birthDate.getYear()) * 12;
        monthAge += currentDate.getMonthValue() - birthDate.getMonthValue();
        
        // 如果当前日期的天数小于出生日期的天数，则月龄减1
        if (currentDate.getDayOfMonth() < birthDate.getDayOfMonth()) {
            monthAge--;
        }
        
        // 确保月龄不小于0
        return Math.max(0, monthAge);
    }

    /**
     * 计算发育商
     * 将智龄除以实际月龄，再乘100，小数点后一位四舍五入取整数
     */
    private int calculateDevelopmentQuotient(double mentalAge, double actualAge) {
        if (actualAge <= 0) {
            return 0;
        }
        
        double quotient = (mentalAge / actualAge) * 100;
        
        // 四舍五入到整数
        return (int) Math.round(quotient);
    }

    /**
     * 确定发育商评级
     * 发育商参考范围：＞130为优秀，100-129为良好，80-109为中等，70-79为临界偏低，＜70为智力发育障碍
     */
    private String determineDevelopmentLevel(int developmentQuotient) {
        if (developmentQuotient > 130) {
            return "优秀";
        } else if (developmentQuotient >= 100) {
            return "良好";
        } else if (developmentQuotient >= 80) {
            return "中等";
        } else if (developmentQuotient >= 70) {
            return "临界偏低";
        } else {
            return "智力发育障碍";
        }
    }

    /**
     * 生成结果摘要
     */
    private AssessmentResultDTO.ResultSummary generateSummary(double totalMentalAge, double actualAge, int developmentQuotient, String developmentLevel) {
        String interpretation = String.format("总的智龄为%.1f个月，实际月龄为%.1f个月，发育商为%d，评级为%s。", 
                totalMentalAge, actualAge, developmentQuotient, developmentLevel);
        
        return AssessmentResultDTO.ResultSummary.builder()
                .label("儿童能力测评结果")
                .value(developmentQuotient)
                .level(developmentLevel)
                .range(new int[]{0, 200})
                .description("基于五个相关问卷的综合评估")
                .interpretation(interpretation)
                .advice(generateAdvice(developmentQuotient))
                .build();
    }

    /**
     * 生成结果详情
     */
    private List<AssessmentResultDTO.ResultDetail> generateDetails(double totalMentalAge, double actualAge, int developmentQuotient, String developmentLevel, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        List<AssessmentResultDTO.ResultDetail> details = new ArrayList<>();
        
        // 添加总的智龄详情
        AssessmentResultDTO.ResultDetail mentalAgeDetail = AssessmentResultDTO.ResultDetail.builder()
                .label("总的智龄")
                .value((int) totalMentalAge)
                .level("")
                .range(new int[]{0, 100})
                .interpretation(String.format("总的智龄为%.1f个月，由五个相关问卷的得分计算得出（总分/5）。", totalMentalAge))
                .build();
        details.add(mentalAgeDetail);
        
        // 添加实际月龄详情
        AssessmentResultDTO.ResultDetail actualAgeDetail = AssessmentResultDTO.ResultDetail.builder()
                .label("实际月龄")
                .value((int) actualAge)
                .level("")
                .range(new int[]{0, 100})
                .interpretation(String.format("实际月龄为%.1f个月，根据宝宝出生日期计算得出。", actualAge))
                .build();
        details.add(actualAgeDetail);
        
        // 添加发育商详情
        AssessmentResultDTO.ResultDetail developmentQuotientDetail = AssessmentResultDTO.ResultDetail.builder()
                .label("发育商")
                .value(developmentQuotient)
                .level(developmentLevel)
                .range(new int[]{0, 200})
                .interpretation(String.format("发育商为%d，计算公式：智龄(%.1f) ÷ 实际月龄(%.1f) × 100 = %d，评级为%s。", 
                        developmentQuotient, totalMentalAge, actualAge, developmentQuotient, developmentLevel))
                .build();
        details.add(developmentQuotientDetail);
        
        // 添加各问卷详情
        for (AssessmentResultDTO.QuestionnaireResultItem item : questionnaireResults) {
            AssessmentResultDTO.ResultDetail detail = AssessmentResultDTO.ResultDetail.builder()
                    .label(item.getQuestionnaireName() != null ? item.getQuestionnaireName() : "问卷" + item.getQuestionnaireId())
                    .value(item.getScore() != null ? item.getScore().intValue() : 0)
                    .level(item.getLevel() != null ? item.getLevel() : "")
                    .range(new int[]{0, 100})
                    .interpretation(String.format("该问卷得分为%.1f分，评级为%s", 
                            item.getScore() != null ? item.getScore().doubleValue() : 0.0,
                            item.getLevel() != null ? item.getLevel() : "未评级"))
                    .build();
            details.add(detail);
        }
        
        return details;
    }

    /**
     * 生成建议
     */
    private AssessmentResultDTO.Advice generateAdvice(int developmentQuotient) {
        String description;
        List<String> content;
        
        if (developmentQuotient > 130) {
            description = "发育商优秀，表现卓越";
            content = Arrays.asList(
                "儿童能力发展优秀，继续保持良好的发展状态",
                "可以适当增加一些挑战性的活动和训练",
                "定期进行测评以跟踪发展进度",
                "建议在优势领域进行深入培养"
            );
        } else if (developmentQuotient >= 100) {
            description = "发育商良好，发展正常";
            content = Arrays.asList(
                "儿童能力发展良好，符合正常发展水平",
                "继续保持当前的发展节奏",
                "可以适当增加一些发展性活动",
                "定期进行测评以跟踪发展进度"
            );
        } else if (developmentQuotient >= 80) {
            description = "发育商中等，需要关注";
            content = Arrays.asList(
                "儿童能力发展处于中等水平，需要适当关注",
                "在薄弱环节进行针对性训练",
                "增加相关领域的练习时间",
                "寻求专业指导以提升能力"
            );
        } else if (developmentQuotient >= 70) {
            description = "发育商临界偏低，需要重点关注";
            content = Arrays.asList(
                "儿童能力发展偏低，需要重点关注",
                "制定详细的训练计划",
                "寻求专业评估和指导",
                "增加练习频率和强度"
            );
        } else {
            description = "发育商偏低，建议专业干预";
            content = Arrays.asList(
                "儿童能力发展明显偏低，建议寻求专业医生或治疗师评估",
                "制定个性化的干预计划",
                "定期跟踪和调整治疗方案",
                "建议进行全面的发展评估"
            );
        }
        
        return AssessmentResultDTO.Advice.builder()
                .description(description)
                .content(content)
                .build();
    }

    /**
     * 生成测评报告
     */
    private String generateReport(double totalMentalAge, double actualAge, int developmentQuotient, String developmentLevel, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        StringBuilder report = new StringBuilder();
        report.append("儿童能力测评报告\n\n");
        
        report.append("总体评估：\n");
        report.append(String.format("本次测评总的智龄为%.1f个月，实际月龄为%.1f个月，发育商为%d，评级为%s。\n\n", 
                totalMentalAge, actualAge, developmentQuotient, developmentLevel));
        
        report.append("计算公式：\n");
        report.append(String.format("总的智龄 = 五个问卷得分之和 ÷ 5 = %.1f个月\n", totalMentalAge));
        report.append(String.format("发育商 = 智龄(%.1f) ÷ 实际月龄(%.1f) × 100 = %d\n\n", totalMentalAge, actualAge, developmentQuotient));
        
        report.append("各问卷表现：\n");
        for (AssessmentResultDTO.QuestionnaireResultItem item : questionnaireResults) {
            String questionnaireName = item.getQuestionnaireName() != null ? item.getQuestionnaireName() : "问卷" + item.getQuestionnaireId();
            double score = item.getScore() != null ? item.getScore().doubleValue() : 0.0;
            String level = item.getLevel() != null ? item.getLevel() : "未评级";
            report.append(String.format("- %s：%.1f分（%s）\n", questionnaireName, score, level));
        }
        
        report.append("\n建议：\n");
        if (developmentQuotient > 130) {
            report.append("发育商优秀，建议继续保持良好的发展状态，可以适当增加一些挑战性的活动，在优势领域进行深入培养。");
        } else if (developmentQuotient >= 100) {
            report.append("发育商良好，建议继续保持当前的发展节奏，可以适当增加一些发展性活动。");
        } else if (developmentQuotient >= 80) {
            report.append("发育商中等，建议在薄弱环节进行针对性训练，增加相关领域的练习时间，寻求专业指导以提升能力。");
        } else if (developmentQuotient >= 70) {
            report.append("发育商临界偏低，建议制定详细的训练计划，寻求专业评估和指导，增加练习频率和强度。");
        } else {
            report.append("发育商偏低，建议寻求专业医生或治疗师评估，制定个性化的干预计划，进行全面的发展评估。");
        }
        
        return report.toString();
    }
} 
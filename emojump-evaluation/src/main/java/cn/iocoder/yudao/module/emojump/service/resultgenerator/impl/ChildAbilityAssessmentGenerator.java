package cn.iocoder.yudao.module.emojump.service.resultgenerator.impl;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.AbstractQuestionnaireResultGenerator;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireResultDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 儿童能力评估量表结果生成器
 * 支持5个问卷的智龄计算
 *
 * @author 芋道源码
 */
@Component
public class ChildAbilityAssessmentGenerator extends AbstractQuestionnaireResultGenerator {

    // 支持的问卷ID列表
    private static final List<Long> SUPPORTED_QUESTIONNAIRE_IDS = Arrays.asList(17L, 18L, 19L, 20L, 21L);
    private static final String QUESTIONNAIRE_NAME = "儿童能力评估量表";

    public boolean isSupported(Long questionnaireId) {
        return SUPPORTED_QUESTIONNAIRE_IDS.contains(questionnaireId);
    }

    @Override
    public List<Long> getSupportedQuestionnaireIds() {
        return SUPPORTED_QUESTIONNAIRE_IDS;
    }

    @Override
    public Long getSupportedQuestionnaireId() {
        return SUPPORTED_QUESTIONNAIRE_IDS.get(0); // 返回第一个作为默认
    }

    @Override
    public String getQuestionnaireName() {
        return QUESTIONNAIRE_NAME;
    }

    @Override
    protected QuestionnaireResultDTO calculateResult(QuestionnaireAnswerDTO answerDTO) {
        // 1. 计算基线月龄
        double baselineAge = calculateBaselineAge(answerDTO.getAnswers());

        // 2. 计算通过项目的累计分数
        double passedScore = calculatePassedScore(answerDTO.getAnswers(), baselineAge);

        // 3. 计算智龄
        double mentalAge = baselineAge + passedScore;

        // 4. 生成结果摘要
        QuestionnaireResultDTO.ResultSummary summary = generateSummary(mentalAge);

        // 5. 生成详细结果
        List<QuestionnaireResultDTO.ResultDetail> details = generateDetails(mentalAge, answerDTO.getAnswers());

        // 6. 构建resultData
        String resultData = buildResultData(summary, details);

        return QuestionnaireResultDTO.builder()
                .score(BigDecimal.valueOf(mentalAge))
                .level("") // 智龄评估不需要评级
                .resultData(resultData)
                .summary(summary)
                .details(details)
                .build();
    }

    /**
     * 计算基线月龄
     * 按index降序排序，找到相邻的不同数值的index，如果都是"能做到"，取较小的为基线月龄
     */
    private double calculateBaselineAge(List<QuestionnaireAnswerDTO.AnswerItem> answers) {
        // 按index分组
        Map<Integer, List<QuestionnaireAnswerDTO.AnswerItem>> indexGroups = new HashMap<>();
        for (QuestionnaireAnswerDTO.AnswerItem answer : answers) {
            indexGroups.computeIfAbsent(answer.getIndex(), k -> new ArrayList<>()).add(answer);
        }

        // 获取所有不同的index，按降序排列
        List<Integer> sortedIndexes = new ArrayList<>(indexGroups.keySet());
        sortedIndexes.sort(Comparator.reverseOrder());

        // 检查相邻的不同数值的index
        for (int i = 0; i < sortedIndexes.size() - 1; i++) {
            int currentIndex = sortedIndexes.get(i);
            int nextIndex = sortedIndexes.get(i + 1);

            // 检查当前index的所有答案都是"能做到"
            List<QuestionnaireAnswerDTO.AnswerItem> currentGroup = indexGroups.get(currentIndex);
            boolean currentAllCanDo = currentGroup.stream().allMatch(answer -> "能做到".equals(answer.getAnswer()));

            // 检查下一个index的所有答案都是"能做到"
            List<QuestionnaireAnswerDTO.AnswerItem> nextGroup = indexGroups.get(nextIndex);
            boolean nextAllCanDo = nextGroup.stream().allMatch(answer -> "能做到".equals(answer.getAnswer()));

            // 如果两个index的所有答案都是"能做到"，返回较小的index作为基线月龄
            if (currentAllCanDo && nextAllCanDo) {
                return nextIndex;
            }
        }

        // 如果没有找到，返回0
        return 0;
    }

    /**
     * 计算通过项目的累计分数
     * 计算所有"能做到"题目的分数总和
     */
    private double calculatePassedScore(List<QuestionnaireAnswerDTO.AnswerItem> answers, double baselineAge) {
        double totalScore = 0.0;

        // 按月龄分组统计每个月龄的总题目数量和"能做到"的题目数量
        Map<Integer, Integer> totalQuestionCount = new HashMap<>();
        Map<Integer, Integer> canDoQuestionCount = new HashMap<>();
        
        for (QuestionnaireAnswerDTO.AnswerItem answer : answers) {
            // 统计总题目数量
            totalQuestionCount.put(answer.getIndex(), totalQuestionCount.getOrDefault(answer.getIndex(), 0) + 1);
            // 统计"能做到"的题目数量
            if ("能做到".equals(answer.getAnswer())) {
                canDoQuestionCount.put(answer.getIndex(), canDoQuestionCount.getOrDefault(answer.getIndex(), 0) + 1);
            }
        }

        // 计算每个月龄的分数
        for (Map.Entry<Integer, Integer> entry : canDoQuestionCount.entrySet()) {
            int age = entry.getKey();
            int canDoCount = entry.getValue();
            int totalCount = totalQuestionCount.get(age);

            double scorePerQuestion = getScorePerQuestion(age, totalCount);
            double ageScore = scorePerQuestion * canDoCount;
            totalScore += ageScore;
        }
        return totalScore;
    }

    /**
     * 根据月龄和题目数量获取每题分数
     */
    private double getScorePerQuestion(int age, int questionCount) {
        if (age >= 1 && age <= 12) {
            // 1-12月龄：每个月龄总分1分
            return 1.0 / questionCount;
        } else if (age >= 15 && age <= 36) {
            // 15-36月龄：每个月龄总分3分
            return 3.0 / questionCount;
        } else if (age >= 42 && age <= 84) {
            // 42-84月龄：每个月龄总分3分
            return 3.0 / questionCount;
        } else {
            // 其他月龄默认1分
            return 1.0 / questionCount;
        }
    }

    /**
     * 生成结果摘要
     */
    private QuestionnaireResultDTO.ResultSummary generateSummary(double mentalAge) {
        String interpretation = getMentalAgeInterpretation(mentalAge);
        int[] range = getSummaryRange(mentalAge);
        String description = getSummaryDescription(mentalAge);
        QuestionnaireResultDTO.Advice advice = generateSummaryAdvice(mentalAge);

        return QuestionnaireResultDTO.ResultSummary.builder()
                .label("智龄评估")
                .value((int) mentalAge)
                .level("")
                .range(range)
                .description(description)
                .interpretation(interpretation)
                .advice(advice)
                .build();
    }

    /**
     * 生成详细结果
     */
    private List<QuestionnaireResultDTO.ResultDetail> generateDetails(double mentalAge, List<QuestionnaireAnswerDTO.AnswerItem> answers) {
        List<QuestionnaireResultDTO.ResultDetail> details = new ArrayList<>();

        // 智龄详细结果
        details.add(createMentalAgeDetail(mentalAge, answers));

        return details;
    }

    /**
     * 创建智龄详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createMentalAgeDetail(double mentalAge, List<QuestionnaireAnswerDTO.AnswerItem> answers) {
        // 计算基线月龄和累计分数
        double baselineAge = calculateBaselineAge(answers);
        double passedScore = calculatePassedScore(answers, baselineAge);
        
        // 构建详细的计算公式字符串
        String detailedFormula = buildDetailedFormula(answers, baselineAge, passedScore);
        String formula = String.format("智龄 = %.0f + %.1f = %.1f个月", baselineAge, passedScore, mentalAge);
        String interpretation = String.format("智龄为%.1f个月，计算公式：%s，详细计算：%s，表示儿童在认知发展方面的能力水平。", mentalAge, formula, detailedFormula);

        return QuestionnaireResultDTO.ResultDetail.builder()
                .label("智龄")
                .value((int) mentalAge)
                .level("")
                .range(new int[]{0, 100})
                .interpretation(interpretation)
                .build();
    }

    /**
     * 获取智龄解释
     */
    private String getMentalAgeInterpretation(double mentalAge) {
        return String.format("智龄为%.1f个月，建议结合儿童实际年龄进行综合评估。", mentalAge);
    }

    /**
     * 获取总分的阈值范围
     */
    private int[] getSummaryRange(double mentalAge) {
        return new int[]{0, 100};
    }

    /**
     * 获取总分的描述
     */
    private String getSummaryDescription(double mentalAge) {
        return "智龄评估完成，建议结合儿童实际年龄进行综合评估。";
    }

    /**
     * 生成总分的建议
     */
    private QuestionnaireResultDTO.Advice generateSummaryAdvice(double mentalAge) {
        List<String> content = new ArrayList<>();
        String description = "智龄评估建议";
        
        content.add("建议提供儿童实际年龄信息，以便进行更准确的评估");
        content.add("智龄评估结果仅供参考，建议结合其他评估工具");
        content.add("如有疑问，建议咨询专业儿童发展专家");

        return QuestionnaireResultDTO.Advice.builder()
                .description(description)
                .content(content)
                .build();
    }

    /**
     * 构建详细的计算公式字符串
     */
    private String buildDetailedFormula(List<QuestionnaireAnswerDTO.AnswerItem> answers, double baselineAge, double passedScore) {
        StringBuilder formula = new StringBuilder();
        
        // 按月龄分组统计每个月龄的总题目数量和"能做到"的题目数量
        Map<Integer, Integer> totalQuestionCount = new HashMap<>();
        Map<Integer, Integer> canDoQuestionCount = new HashMap<>();
        
        for (QuestionnaireAnswerDTO.AnswerItem answer : answers) {
            // 统计总题目数量
            totalQuestionCount.put(answer.getIndex(), totalQuestionCount.getOrDefault(answer.getIndex(), 0) + 1);
            // 统计"能做到"的题目数量
            if ("能做到".equals(answer.getAnswer())) {
                canDoQuestionCount.put(answer.getIndex(), canDoQuestionCount.getOrDefault(answer.getIndex(), 0) + 1);
            }
        }
        
        // 按index降序排列
        List<Integer> sortedIndexes = new ArrayList<>(canDoQuestionCount.keySet());
        sortedIndexes.sort(Comparator.reverseOrder());
        
        formula.append("累计分数 = ");
        boolean first = true;
        
        for (Integer index : sortedIndexes) {
            int canDoCount = canDoQuestionCount.get(index);
            int totalCount = totalQuestionCount.get(index);
            double scorePerQuestion = getScorePerQuestion(index, totalCount);
            double totalScore = scorePerQuestion * canDoCount;
            
            if (!first) {
                formula.append(" + ");
            }
            formula.append(String.format("index%d(%d题×%.1f分=%.1f分)", index, canDoCount, scorePerQuestion, totalScore));
            first = false;
        }
        
        formula.append(String.format(" = %.1f分", passedScore));
        
        return formula.toString();
    }

    /**
     * 构建resultData JSON字符串
     */
    private String buildResultData(QuestionnaireResultDTO.ResultSummary summary, List<QuestionnaireResultDTO.ResultDetail> details) {
        Map<String, Object> resultData = new HashMap<>();
        resultData.put("summary", summary);
        resultData.put("details", details);
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(resultData);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}

package cn.iocoder.yudao.module.emojump.service.resultgenerator.impl;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.questionnaire.AbstractQuestionnaireResultGenerator;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireResultDTO;
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
        QuestionnaireResultDTO.ResultSummary summary = generateSummary(mentalAge, answerDTO.getQuestionnaireId());
        
        // 5. 生成详细结果
        List<QuestionnaireResultDTO.ResultDetail> details = generateDetails(mentalAge, answerDTO.getAnswers(), answerDTO.getQuestionnaireId());
        
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
    private QuestionnaireResultDTO.ResultSummary generateSummary(double mentalAge, Long questionnaireId) {
        String interpretation = getMentalAgeInterpretation(mentalAge, questionnaireId);
        int[] range = getSummaryRange(mentalAge, questionnaireId);
        String description = getSummaryDescription(mentalAge, questionnaireId);
        QuestionnaireResultDTO.Advice advice = generateSummaryAdvice(mentalAge, questionnaireId);

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
    private List<QuestionnaireResultDTO.ResultDetail> generateDetails(double mentalAge, List<QuestionnaireAnswerDTO.AnswerItem> answers, Long questionnaireId) {
        List<QuestionnaireResultDTO.ResultDetail> details = new ArrayList<>();

        // 智龄详细结果
        details.add(createMentalAgeDetail(mentalAge, answers, questionnaireId));

        return details;
    }

    /**
     * 创建智龄详细结果
     */
    private QuestionnaireResultDTO.ResultDetail createMentalAgeDetail(double mentalAge, List<QuestionnaireAnswerDTO.AnswerItem> answers, Long questionnaireId) {
        // 计算基线月龄和累计分数
        double baselineAge = calculateBaselineAge(answers);
        double passedScore = calculatePassedScore(answers, baselineAge);
        
        // 根据问卷ID生成不同的描述
        String interpretation = getMentalAgeDetailInterpretation(mentalAge, baselineAge, passedScore, questionnaireId);

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
    private String getMentalAgeInterpretation(double mentalAge, Long questionnaireId) {
        String base;
        switch (questionnaireId != null ? questionnaireId.intValue() : -1) {
            case 17:
                base = String.format("精细动作智龄为%.1f个月。", mentalAge)
                    + " 精细动作能力反映了孩子手部小肌肉的灵活性和协调性，是日常生活自理、书写绘画等能力的基础。"
                    + " 如果智龄与实际年龄基本相符，说明孩子精细动作发展良好。"
                    + " 若智龄明显低于实际年龄，建议家长多安排手工、拼插、绘画等活动，耐心引导孩子多练习，必要时可咨询康复或早教专业人士。";
                break;
            case 18:
                base = String.format("适应能力智龄为%.1f个月。", mentalAge)
                    + " 适应能力体现了孩子的生活自理和环境应对能力。"
                    + " 智龄与实际年龄相符，说明孩子能较好地独立完成日常任务。"
                    + " 若智龄偏低，建议家长逐步放手，让孩子多参与穿衣、如厕、整理物品等生活实践，增强独立性。";
                break;
            case 19:
                base = String.format("语言能力智龄为%.1f个月。", mentalAge)
                    + " 语言能力是孩子认知、社交和学习的基础。"
                    + " 智龄与实际年龄相符，说明孩子语言理解和表达能力发展正常。"
                    + " 若智龄偏低，建议家长多与孩子交流、讲故事、鼓励表达，必要时可寻求语言训练师帮助。";
                break;
            case 20:
                base = String.format("社会行为智龄为%.1f个月。", mentalAge)
                    + " 社会行为能力反映了孩子与同伴交往、合作、情绪管理等方面的发展。"
                    + " 智龄与实际年龄相符，说明孩子能较好地适应集体生活。"
                    + " 若智龄偏低，建议家长多带孩子参与集体活动，教会其分享、轮流、表达情绪，帮助其建立良好的人际关系。";
                break;
            case 21:
                base = String.format("大运动智龄为%.1f个月。", mentalAge)
                    + " 大运动能力是孩子身体健康、空间感知和平衡能力的体现。"
                    + " 智龄与实际年龄相符，说明孩子运动发育良好。"
                    + " 若智龄偏低，建议家长多安排户外运动，鼓励孩子尝试多种大运动项目，提升体能和协调性。";
                break;
            default:
                base = String.format("智龄为%.1f个月。", mentalAge)
                    + " 智龄是对孩子当前能力发展的一个参考指标，建议结合实际年龄和日常表现综合评估。"
                    + " 如有疑问或发现发展迟缓，建议及时咨询专业人士。";
        }
        return base;
    }

    /**
     * 获取智龄详细解释
     */
    private String getMentalAgeDetailInterpretation(double mentalAge, double baselineAge, double passedScore, Long questionnaireId) {
        switch (questionnaireId != null ? questionnaireId.intValue() : -1) {
            case 17:
                return String.format("精细动作智龄为%.1f个月。基线月龄%.0f个月，累计分数%.1f分。", mentalAge, baselineAge, passedScore)
                    + " 精细动作能力是孩子手部小肌肉协调性的体现，直接影响日常生活自理、书写绘画等能力。"
                    + " 建议家长多安排手工活动，如拼图、串珠、剪纸等，锻炼手指灵活性。";
            case 18:
                return String.format("适应能力智龄为%.1f个月。基线月龄%.0f个月，累计分数%.1f分。", mentalAge, baselineAge, passedScore)
                    + " 适应能力反映了孩子的生活自理和环境应对能力。"
                    + " 建议家长逐步放手，让孩子多参与穿衣、如厕、整理物品等生活实践。";
            case 19:
                return String.format("语言能力智龄为%.1f个月。基线月龄%.0f个月，累计分数%.1f分。", mentalAge, baselineAge, passedScore)
                    + " 语言能力是孩子认知、社交和学习的基础。"
                    + " 建议家长多与孩子交流、讲故事、鼓励表达，丰富语言环境。";
            case 20:
                return String.format("社会行为智龄为%.1f个月。基线月龄%.0f个月，累计分数%.1f分。", mentalAge, baselineAge, passedScore)
                    + " 社会行为能力反映了孩子与同伴交往、合作、情绪管理等方面的发展。"
                    + " 建议家长多带孩子参与集体活动，教会其分享、轮流、表达情绪。";
            case 21:
                return String.format("大运动智龄为%.1f个月。基线月龄%.0f个月，累计分数%.1f分。", mentalAge, baselineAge, passedScore)
                    + " 大运动能力是孩子身体健康、空间感知和平衡能力的体现。"
                    + " 建议家长多安排户外运动，鼓励孩子尝试多种大运动项目。";
            default:
                return String.format("智龄为%.1f个月。基线月龄%.0f个月，累计分数%.1f分。", mentalAge, baselineAge, passedScore)
                    + " 智龄是对孩子当前能力发展的一个参考指标，建议结合实际年龄和日常表现综合评估。";
        }
    }

    /**
     * 获取总分的阈值范围
     */
    private int[] getSummaryRange(double mentalAge, Long questionnaireId) {
        return new int[]{0, 100};
    }

    /**
     * 获取总分的描述
     */
    private String getSummaryDescription(double mentalAge, Long questionnaireId) {
        switch (questionnaireId != null ? questionnaireId.intValue() : -1) {
            case 17: return "儿童精细动作能力是指手部小肌肉群的灵活性、协调性及操作能力，如抓握、捏取、拼插、绘画等。精细动作的发展直接影响孩子日常生活自理、学习书写、绘画等能力，是认知和大脑发育的重要基础。";
            case 18: return "儿童适应能力是指孩子对环境变化的应对、自理能力及独立生活能力，包括穿衣、如厕、进食、整理物品等。良好的适应能力有助于孩子顺利融入集体生活，增强自信心和独立性。";
            case 19: return "儿童语言能力包括语言理解和表达，是认知、社交和学习的基础。良好的语言能力有助于孩子表达需求、理解指令、与同伴交流、学习新知识。";
            case 20: return "儿童社会行为能力是指孩子与他人交往、合作、分享、遵守规则、情绪管理等方面的能力。良好的社会行为有助于孩子建立友谊、适应集体生活、形成积极人格。";
            case 21: return "儿童大运动能力是指全身大肌肉群的运动与协调能力，如走、跑、跳、攀爬、投掷等。大运动的发展有助于孩子身体健康、空间感知、平衡能力和自信心的提升。";
            default: return "智龄评估完成，建议结合儿童实际年龄进行综合评估。";
        }
    }

    /**
     * 生成总分的建议
     */
    private QuestionnaireResultDTO.Advice generateSummaryAdvice(double mentalAge, Long questionnaireId) {
        List<String> content = new ArrayList<>();
        String description;
        switch (questionnaireId != null ? questionnaireId.intValue() : -1) {
            case 17:
                description = "精细动作能力提升建议";
                content.add("鼓励孩子多做手工活动，如拼图、串珠、剪纸、折纸、橡皮泥捏塑等，锻炼手指灵活性。");
                content.add("日常生活中让孩子自己扣纽扣、拉拉链、系鞋带、用勺子吃饭等，提升自理能力。");
                content.add("提供画笔、彩泥等工具，鼓励孩子自由绘画、涂鸦，发展手眼协调。");
                content.add("避免过度包办，给予孩子尝试和练习的机会。");
                break;
            case 18:
                description = "适应能力提升建议";
                content.add("鼓励孩子自己穿脱衣服、鞋袜，逐步减少成人帮助。");
                content.add("让孩子参与简单家务，如收拾玩具、整理床铺、擦桌子等，培养责任感。");
                content.add("训练孩子独立如厕、洗手、刷牙等生活技能。");
                content.add("在新环境中多给予鼓励和正向引导，帮助孩子适应变化。");
                break;
            case 19:
                description = "语言能力提升建议";
                content.add("多与孩子对话，耐心倾听并鼓励其表达自己的想法和感受。");
                content.add("每天坚持亲子共读，讲故事、看图书，丰富词汇和表达能力。");
                content.add("鼓励孩子描述日常见闻、讲述经历，锻炼叙述能力。");
                content.add("通过儿歌、绕口令、角色扮演等游戏提升语言兴趣。");
                break;
            case 20:
                description = "社会行为能力提升建议";
                content.add("多带孩子参与集体活动，如亲子班、兴趣小组、户外游戏，锻炼社交能力。");
                content.add("鼓励孩子学会分享、轮流、等待，理解并遵守游戏规则。");
                content.add("关注孩子情绪变化，教会其用语言表达情绪，学会自我调节。");
                content.add("家长以身作则，示范良好的人际交往方式。");
                break;
            case 21:
                description = "大运动能力提升建议";
                content.add("鼓励孩子多进行户外运动，如跑步、跳绳、踢球、骑车、攀爬等，增强体能。");
                content.add("创设安全的运动环境，让孩子自由探索和尝试各种大运动项目。");
                content.add("亲子一起参与运动，提升孩子的运动兴趣和坚持性。");
                content.add("关注孩子动作发展进程，发现异常及时咨询专业人士。");
                break;
            default:
                description = "智龄评估建议";
                content.add("建议提供儿童实际年龄信息，以便进行更准确的评估。");
                content.add("智龄评估结果仅供参考，建议结合其他评估工具。");
                content.add("如有疑问，建议咨询专业儿童发展专家。");
        }
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

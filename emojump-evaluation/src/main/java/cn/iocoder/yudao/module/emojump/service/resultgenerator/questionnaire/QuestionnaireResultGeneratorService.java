package cn.iocoder.yudao.module.emojump.service.resultgenerator.questionnaire;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireResultDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问卷结果生成器管理服务
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class QuestionnaireResultGeneratorService {

    private final Map<Long, QuestionnaireResultGenerator> generatorMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired(required = false)
    private List<QuestionnaireResultGenerator> generators;

    @PostConstruct
    public void init() {
        if (generators != null) {
            for (QuestionnaireResultGenerator generator : generators) {
                List<Long> questionnaireIds = generator.getSupportedQuestionnaireIds();
                if (questionnaireIds != null) {
                    for (Long questionnaireId : questionnaireIds) {
                        generatorMap.put(questionnaireId, generator);
                        log.info("[QuestionnaireResultGeneratorService] 注册问卷结果生成器: {} -> {}", 
                                questionnaireId, generator.getQuestionnaireName());
                    }
                }
            }
        }
        log.info("[QuestionnaireResultGeneratorService] 初始化完成，共注册 {} 个结果生成器，支持 {} 个问卷", 
                generators != null ? generators.size() : 0, generatorMap.size());
    }

    /**
     * 根据问卷答案生成结果
     *
     * @param questionnaireId 问卷ID
     * @param answerDataJson 问卷答案JSON数据
     * @return 问卷结果
     */
    public QuestionnaireResultDTO generateResult(Long questionnaireId, String answerDataJson) {
        log.info("[generateResult] 开始生成问卷结果，问卷ID: {}", questionnaireId);

        try {
            // 1. 获取对应的结果生成器
            QuestionnaireResultGenerator generator = getGenerator(questionnaireId);

            // 2. 解析答案数据
            QuestionnaireAnswerDTO answerDTO = parseAnswerData(answerDataJson);

            // 3. 生成结果
            QuestionnaireResultDTO result = generator.generateResult(answerDTO);

            log.info("[generateResult] 问卷结果生成成功，问卷ID: {}, 总分: {}, 评级: {}",
                    questionnaireId, result.getScore(), result.getLevel());

            return result;

        } catch (Exception e) {
            log.error("[generateResult] 问卷结果生成失败，问卷ID: {}, 错误: {}", questionnaireId, e.getMessage(), e);
            throw new RuntimeException("问卷结果生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取问卷对应的结果生成器
     *
     * @param questionnaireId 问卷ID
     * @return 结果生成器
     */
    private QuestionnaireResultGenerator getGenerator(Long questionnaireId) {
        QuestionnaireResultGenerator generator = generatorMap.get(questionnaireId);
        if (generator == null) {
            throw new RuntimeException("未找到问卷ID " + questionnaireId + " 对应的结果生成器");
        }
        return generator;
    }

    /**
     * 解析答案数据
     *
     * @param answerDataJson 答案JSON数据
     * @return 答案DTO
     */
    private QuestionnaireAnswerDTO parseAnswerData(String answerDataJson) {
        try {
            // 解析JSON数组格式的答案数据
            QuestionnaireAnswerDTO.AnswerItem[] answerArray =
                    objectMapper.readValue(answerDataJson, QuestionnaireAnswerDTO.AnswerItem[].class);

            return QuestionnaireAnswerDTO.builder()
                    .answers(Arrays.asList(answerArray))
                    .build();

        } catch (Exception e) {
            log.error("[parseAnswerData] 解析答案数据失败: {}", e.getMessage(), e);
            throw new RuntimeException("解析答案数据失败", e);
        }
    }

    /**
     * 检查是否支持指定问卷
     *
     * @param questionnaireId 问卷ID
     * @return 是否支持
     */
    public boolean isSupported(Long questionnaireId) {
        return generatorMap.containsKey(questionnaireId);
    }

    /**
     * 获取所有支持的问卷ID
     *
     * @return 问卷ID列表
     */
    public List<Long> getSupportedQuestionnaireIds() {
        return new ArrayList<>(generatorMap.keySet());
    }

    /**
     * 获取问卷生成器信息
     *
     * @param questionnaireId 问卷ID
     * @return 生成器名称
     */
    public String getGeneratorInfo(Long questionnaireId) {
        QuestionnaireResultGenerator generator = generatorMap.get(questionnaireId);
        return generator != null ? generator.getQuestionnaireName() : "未知";
    }
}

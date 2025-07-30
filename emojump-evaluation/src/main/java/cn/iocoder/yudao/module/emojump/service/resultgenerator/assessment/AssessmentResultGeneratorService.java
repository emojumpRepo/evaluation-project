package cn.iocoder.yudao.module.emojump.service.resultgenerator.assessment;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.assessment.AssessmentResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测评结果生成器管理服务
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AssessmentResultGeneratorService {

    private final Map<Long, AssessmentResultGenerator> generatorMap = new HashMap<>();

    @Autowired(required = false)
    private List<AssessmentResultGenerator> generators;

    @PostConstruct
    public void init() {
        if (generators != null) {
            for (AssessmentResultGenerator generator : generators) {
                List<Long> assessmentIds = generator.getSupportedAssessmentIds();
                if (assessmentIds != null) {
                    for (Long assessmentId : assessmentIds) {
                        generatorMap.put(assessmentId, generator);
                        log.info("[AssessmentResultGeneratorService] 注册测评结果生成器: {} -> {}", 
                                assessmentId, generator.getAssessmentName());
                    }
                }
            }
        }
        log.info("[AssessmentResultGeneratorService] 初始化完成，共注册 {} 个结果生成器，支持 {} 个测评", 
                generators != null ? generators.size() : 0, generatorMap.size());
    }

    /**
     * 根据测评ID和宝宝ID生成测评结果
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @param questionnaireResults 问卷结果列表
     * @return 测评结果
     */
    public AssessmentResultDTO generateResult(Long assessmentId, Long babyId, List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResults) {
        log.info("[generateResult] 开始生成测评结果，测评ID: {}, 宝宝ID: {}", assessmentId, babyId);

        try {
            // 1. 获取对应的结果生成器
            AssessmentResultGenerator generator = getGenerator(assessmentId);

            // 2. 生成结果
            AssessmentResultDTO result = generator.generateResult(assessmentId, babyId, questionnaireResults);

            log.info("[generateResult] 测评结果生成成功，测评ID: {}, 总分: {}, 评级: {}",
                    assessmentId, result.getOverallScore(), result.getOverallLevel());

            return result;

        } catch (Exception e) {
            log.error("[generateResult] 测评结果生成失败，测评ID: {}, 错误: {}", assessmentId, e.getMessage(), e);
            throw new RuntimeException("测评结果生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取测评对应的结果生成器
     *
     * @param assessmentId 测评ID
     * @return 结果生成器
     */
    private AssessmentResultGenerator getGenerator(Long assessmentId) {
        AssessmentResultGenerator generator = generatorMap.get(assessmentId);
        if (generator == null) {
            throw new RuntimeException("未找到测评ID " + assessmentId + " 对应的结果生成器");
        }
        return generator;
    }

    /**
     * 检查是否支持指定测评
     *
     * @param assessmentId 测评ID
     * @return 是否支持
     */
    public boolean isSupported(Long assessmentId) {
        return generatorMap.containsKey(assessmentId);
    }

    /**
     * 获取所有支持的测评ID
     *
     * @return 测评ID列表
     */
    public List<Long> getSupportedAssessmentIds() {
        return new ArrayList<>(generatorMap.keySet());
    }

    /**
     * 获取测评生成器信息
     *
     * @param assessmentId 测评ID
     * @return 生成器名称
     */
    public String getGeneratorInfo(Long assessmentId) {
        AssessmentResultGenerator generator = generatorMap.get(assessmentId);
        return generator != null ? generator.getAssessmentName() : "未知";
    }
} 
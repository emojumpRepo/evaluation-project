package cn.iocoder.yudao.module.emojump.service.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireAnswerSubmitReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultVO;
import cn.iocoder.yudao.module.emojump.convert.questionnaireresult.QuestionnaireResultConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaireresult.EmoQuestionnaireResultMapper;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.questionnaire.QuestionnaireResultGeneratorService;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireResultDTO;
import cn.iocoder.yudao.module.emojump.util.AesDecryptUtil;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentQuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentResultMapper;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppBabyQuestionnaireResultRespVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentResultDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentQuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emojump.enums.ErrorCodeConstants.QUESTIONNAIRE_NOT_EXISTS;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultListRespVO;

/**
 * 问卷结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class QuestionnaireResultServiceImpl implements QuestionnaireResultService {

    @Resource
    private EmoQuestionnaireResultMapper emoQuestionnaireResultMapper;

    @Resource
    private MemberBabyService memberBabyService;

    @Resource
    private AssessmentMapper assessmentMapper;

    @Resource
    private QuestionnaireMapper questionnaireMapper;

    @Resource
    private QuestionnaireResultGeneratorService resultGeneratorService;

    @Resource
    private AssessmentQuestionnaireMapper assessmentQuestionnaireMapper;

    @Resource
    private AssessmentResultMapper assessmentResultMapper;

    @Override
    public Long createQuestionnaireResult(@Valid QuestionnaireResultCreateReqVO createReqVO) {
        // 插入
        EmoQuestionnaireResultDO questionnaireResult = QuestionnaireResultConvert.INSTANCE.convert(createReqVO);
        emoQuestionnaireResultMapper.insert(questionnaireResult);

        log.info("[createQuestionnaireResult] 创建问卷结果成功，ID: {}, 问卷ID: {}, 测评ID: {}, 宝宝ID: {}, 测评结果ID: {}",
                questionnaireResult.getId(), createReqVO.getQuestionnaireId(), createReqVO.getAssessmentId(),
                createReqVO.getBabyId(), createReqVO.getAssessmentResultId());

        // 返回
        return questionnaireResult.getId();
    }

    @Override
    public void updateQuestionnaireResult(@Valid QuestionnaireResultUpdateReqVO updateReqVO) {
        // 校验存在
        validateQuestionnaireResultExists(updateReqVO.getId());

        // 更新
        EmoQuestionnaireResultDO updateObj = QuestionnaireResultConvert.INSTANCE.convert(updateReqVO);
        emoQuestionnaireResultMapper.updateById(updateObj);

        log.info("[updateQuestionnaireResult] 更新问卷结果成功，ID: {}", updateReqVO.getId());
    }

    @Override
    public void deleteQuestionnaireResult(Long id) {
        // 校验存在
        validateQuestionnaireResultExists(id);
        
        // 删除
        emoQuestionnaireResultMapper.deleteById(id);
        
        log.info("[deleteQuestionnaireResult] 删除问卷结果成功，ID: {}", id);
    }

    private void validateQuestionnaireResultExists(Long id) {
        if (emoQuestionnaireResultMapper.selectById(id) == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }
    }

    /**
     * 校验宝宝数据是否存在
     *
     * @param babyId 宝宝ID
     */
    private void validateBabyExists(Long babyId) {
        if (babyId == null || babyId <= 0) {
            log.error("[validateBabyExists] 宝宝ID无效，babyId: {}", babyId);
            throw new RuntimeException("宝宝ID无效，babyId: " + babyId);
        }

        try {
            if (memberBabyService.getBaby(babyId) == null) {
                log.error("[validateBabyExists] 宝宝数据不存在，babyId: {}", babyId);
                throw new RuntimeException("宝宝数据不存在，babyId: " + babyId);
            }
            log.debug("[validateBabyExists] 宝宝数据校验通过，babyId: {}", babyId);
        } catch (Exception e) {
            if (e.getMessage().contains("宝宝数据不存在") || e.getMessage().contains("宝宝ID无效")) {
                throw e;
            }
            log.error("[validateBabyExists] 宝宝数据校验异常，babyId: {}, error: {}", babyId, e.getMessage(), e);
            throw new RuntimeException("宝宝数据校验失败，babyId: " + babyId, e);
        }
    }

    /**
     * 校验测评数据是否存在
     *
     * @param assessmentId 测评ID
     */
    private void validateAssessmentExists(Long assessmentId) {
        if (assessmentId == null || assessmentId <= 0) {
            log.error("[validateAssessmentExists] 测评ID无效，assessmentId: {}", assessmentId);
            throw new RuntimeException("测评ID无效，assessmentId: " + assessmentId);
        }

        try {
            if (assessmentMapper.selectById(assessmentId) == null) {
                log.error("[validateAssessmentExists] 测评数据不存在，assessmentId: {}", assessmentId);
                throw new RuntimeException("测评数据不存在，assessmentId: " + assessmentId);
            }
            log.debug("[validateAssessmentExists] 测评数据校验通过，assessmentId: {}", assessmentId);
        } catch (Exception e) {
            if (e.getMessage().contains("测评数据不存在") || e.getMessage().contains("测评ID无效")) {
                throw e;
            }
            log.error("[validateAssessmentExists] 测评数据校验异常，assessmentId: {}, error: {}", assessmentId, e.getMessage(), e);
            throw new RuntimeException("测评数据校验失败，assessmentId: " + assessmentId, e);
        }
    }

    /**
     * 校验问卷数据是否存在
     *
     * @param questionnaireId 问卷ID
     */
    private void validateQuestionnaireExists(Long questionnaireId) {
        if (questionnaireId == null || questionnaireId <= 0) {
            log.error("[validateQuestionnaireExists] 问卷ID无效，questionnaireId: {}", questionnaireId);
            throw new RuntimeException("问卷ID无效，questionnaireId: " + questionnaireId);
        }
        try {
            if (questionnaireMapper.selectById(questionnaireId) == null) {
                log.error("[validateQuestionnaireExists] 问卷数据不存在，questionnaireId: {}", questionnaireId);
                throw new RuntimeException("问卷数据不存在，assessmentId: " + questionnaireId);
            }
            log.debug("[validateQuestionnaireExists] 问卷数据校验通过，questionnaireId: {}", questionnaireId);
        } catch (Exception e) {
            if (e.getMessage().contains("问卷数据不存在") || e.getMessage().contains("问卷ID无效")) {
                throw e;
            }
            log.error("[validateQuestionnaireExists] 问卷数据校验异常，questionnaireId: {}, error: {}", questionnaireId, e.getMessage(), e);
            throw new RuntimeException("问卷数据校验失败，questionnaireId: " + questionnaireId, e);
        }
    }

    private void validateAssessmentQuestionnaireExists(Long assessmentId, Long questionnaireId) {
        if (!assessmentQuestionnaireMapper.existsByAssessmentIdAndQuestionnaireId(assessmentId, questionnaireId)) {
            log.error("[validateAssessmentQuestionnaireExists] 问卷不在该测评中，assessmentId: {}, questionnaireId: {}", assessmentId, questionnaireId);
            throw new RuntimeException("问卷不在该测评中，assessmentId: " + assessmentId + ", questionnaireId: " + questionnaireId);
        }
        log.debug("[validateAssessmentQuestionnaireExists] 问卷在该测评中，assessmentId: {}, questionnaireId: {}", assessmentId, questionnaireId);
    }

    /**
     * 处理解密后的数据，移除可能存在的外层双引号
     *
     * @param decryptedData 解密后的原始数据
     * @return 处理后的JSON数据
     */
    private String processDecryptedData(String decryptedData) {
        if (decryptedData == null) {
            return null;
        }

        String trimmed = decryptedData.trim();

        // 如果数据以双引号开头和结尾，且内容是JSON格式，则移除外层双引号
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() > 2) {
            String inner = trimmed.substring(1, trimmed.length() - 1);
            // 处理转义字符
            inner = inner.replace("\\\"", "\"");
            inner = inner.replace("\\\\", "\\");
            log.debug("[processDecryptedData] 移除外层双引号，原数据长度: {}, 处理后长度: {}", trimmed.length(), inner.length());
            return inner;
        }

        return trimmed;
    }

    /**
     * 验证问卷答案数据格式是否有效（支持数组格式）
     *
     * @param answerData 问卷答案数据
     * @return 是否为有效的格式
     */
    private boolean isValidJsonFormat(String answerData) {
        try {
            log.debug("[isValidJsonFormat] 开始验证问卷答案格式，数据长度: {}", answerData != null ? answerData.length() : 0);

            if (answerData == null) {
                log.warn("[isValidJsonFormat] 问卷答案数据为null");
                return false;
            }

            String trimmedData = answerData.trim();
            log.debug("[isValidJsonFormat] 去除空格后数据长度: {}, 前20个字符: {}, 后20个字符: {}",
                    trimmedData.length(),
                    trimmedData.length() > 20 ? trimmedData.substring(0, 20) : trimmedData,
                    trimmedData.length() > 20 ? trimmedData.substring(Math.max(0, trimmedData.length() - 20)) : trimmedData);

            // 检查是否为数组格式 [...]
            boolean isArrayFormat = trimmedData.startsWith("[") && trimmedData.endsWith("]");
            // 检查是否为对象格式 {...}（兼容旧格式）
            boolean isObjectFormat = trimmedData.startsWith("{") && trimmedData.endsWith("}");

            boolean hasValidLength = trimmedData.length() > 2;
            boolean containsRequiredFields = trimmedData.contains("\"title\"") &&
                                           trimmedData.contains("\"answer\"") &&
                                           trimmedData.contains("\"index\"");

            log.debug("[isValidJsonFormat] 验证结果 - 数组格式: {}, 对象格式: {}, 长度>2: {}, 包含必需字段: {}",
                    isArrayFormat, isObjectFormat, hasValidLength, containsRequiredFields);

            // 新格式：数组格式且包含必需字段
            // 兼容旧格式：对象格式
            boolean isValid = hasValidLength && ((isArrayFormat && containsRequiredFields) || isObjectFormat);

            if (!isValid) {
                log.warn("[isValidJsonFormat] 问卷答案格式验证失败，完整数据: [{}]", trimmedData);
            } else {
                log.debug("[isValidJsonFormat] 问卷答案格式验证通过，格式类型: {}", isArrayFormat ? "数组格式" : "对象格式");
            }

            return isValid;

        } catch (Exception e) {
            log.error("[isValidJsonFormat] 问卷答案格式验证异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public EmoQuestionnaireResultDO getQuestionnaireResult(Long id) {
        return emoQuestionnaireResultMapper.selectById(id);
    }

    @Override
    public List<EmoQuestionnaireResultDO> getQuestionnaireResultList(Collection<Long> ids) {
        return emoQuestionnaireResultMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<EmoQuestionnaireResultDO> getQuestionnaireResultPage(QuestionnaireResultPageReqVO pageReqVO) {
        return emoQuestionnaireResultMapper.selectPage(pageReqVO);
    }

    @Override
    public List<EmoQuestionnaireResultDO> getQuestionnaireResultList(QuestionnaireResultPageReqVO exportReqVO) {
        return emoQuestionnaireResultMapper.selectList(exportReqVO);
    }

    @Override
    public List<EmoQuestionnaireResultDO> getQuestionnaireResultListByAssessmentResultId(Long assessmentResultId) {
        return emoQuestionnaireResultMapper.selectListByAssessmentResultId(assessmentResultId);
    }

    @Override
    public List<EmoQuestionnaireResultDO> getQuestionnaireResultListByQuestionnaireId(Long questionnaireId) {
        return emoQuestionnaireResultMapper.selectListByQuestionnaireId(questionnaireId);
    }

    @Override
    public Long getQuestionnaireResultCountByQuestionnaireIdAndTime(Long questionnaireId, LocalDateTime startTime, LocalDateTime endTime) {
        return emoQuestionnaireResultMapper.selectCountByQuestionnaireIdAndTime(questionnaireId, startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQuestionnaireResultBatch(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        
        // 校验存在
        for (Long id : ids) {
            validateQuestionnaireResultExists(id);
        }
        
        // 批量删除
        emoQuestionnaireResultMapper.deleteBatchIds(ids);
        
        log.info("[deleteQuestionnaireResultBatch] 批量删除问卷结果成功，数量: {}", ids.size());
    }

    @Override
    public Long submitQuestionnaireAnswer(@Valid QuestionnaireAnswerSubmitReqVO submitReqVO) {
        log.info("[submitQuestionnaireAnswer] 开始提交问卷答案，接收到加密数据");

        try {
            // 解密ID数据
            Long[] decryptedIds = AesDecryptUtil.decryptIds(
                    submitReqVO.getEncryptedUserId(),
                    submitReqVO.getEncryptedAssessmentId(),
                    submitReqVO.getEncryptedQuestionnaireId()
            );

            Long userId = decryptedIds[0];
            Long assessmentId = decryptedIds[1];
            Long questionnaireId = decryptedIds[2];

            // 验证解密后的ID是否有效
            if (!AesDecryptUtil.areValidIds(userId, assessmentId, questionnaireId)) {
                throw new RuntimeException("解密后的ID数据无效");
            }

            log.info("[submitQuestionnaireAnswer] 解密ID成功，userId: {}, assessmentId: {}, questionnaireId: {}",
                    userId, assessmentId, questionnaireId);

            // 校验相关数据是否存在
            log.debug("[submitQuestionnaireAnswer] 开始校验相关数据是否存在");
            validateBabyExists(userId);
            validateAssessmentExists(assessmentId);
            validateQuestionnaireExists(questionnaireId);

            // 解密问卷答案数据
            String decryptedAnswerData = AesDecryptUtil.decryptAnswerData(submitReqVO.getEncryptedAnswerData());

            log.info("[submitQuestionnaireAnswer] 解密问卷答案数据成功，数据长度: {}, 完整数据: [{}]",
                    decryptedAnswerData != null ? decryptedAnswerData.length() : 0, decryptedAnswerData);

            // 处理解密后数据可能包含外层双引号的情况
            String processedAnswerData = processDecryptedData(decryptedAnswerData);

            log.info("[submitQuestionnaireAnswer] 处理后的数据: [{}]", processedAnswerData);

            // 验证处理后的数据格式
            if (!isValidJsonFormat(processedAnswerData)) {
                log.error("[submitQuestionnaireAnswer] 问卷答案数据格式验证失败！期望格式：[{\"title\":\"...\",\"answer\":\"...\",\"index\":1}]");
                throw new RuntimeException("问卷答案数据格式不正确，期望格式为包含title、answer、index字段的对象数组");
            }

            log.info("[submitQuestionnaireAnswer] 问卷答案数据格式验证通过");

            // 查找未完成的测评结果记录
            AssessmentResultDO unfinishedAssessmentResult = assessmentResultMapper.selectOne(
                new LambdaQueryWrapperX<AssessmentResultDO>()
                    .eq(AssessmentResultDO::getAssessmentId, assessmentId)
                    .eq(AssessmentResultDO::getBabyId, userId)
                    .eq(AssessmentResultDO::getStatus, 0)
                    .orderByDesc(AssessmentResultDO::getId)
                    .last("LIMIT 1")
            );
            Long assessmentResultId = unfinishedAssessmentResult != null ? unfinishedAssessmentResult.getId() : null;
            // 创建问卷结果记录
            EmoQuestionnaireResultDO questionnaireResult = EmoQuestionnaireResultDO.builder()
                    .assessmentId(assessmentId)
                    .assessmentResultId(assessmentResultId)
                    .babyId(userId) // 使用userId作为babyId
                    .questionnaireId(questionnaireId)
                    .answerData(processedAnswerData) // 存储处理后的答案数据
                    .completedTime(submitReqVO.getCompletedTime())
                    .creator(String.valueOf(userId))
                    .updater(String.valueOf(userId))
                    .build();

            // 插入数据库
            emoQuestionnaireResultMapper.insert(questionnaireResult);

            log.info("[submitQuestionnaireAnswer] 问卷答案提交成功，ID: {}, userId: {}, assessmentId: {}, questionnaireId: {}",
                    questionnaireResult.getId(), userId, assessmentId, questionnaireId);

            // 生成问卷结果
            try {
                if (resultGeneratorService.isSupported(questionnaireId)) {
                    log.info("[submitQuestionnaireAnswer] 开始生成问卷结果，问卷ID: {}", questionnaireId);

                    QuestionnaireResultDTO resultDTO = resultGeneratorService.generateResult(
                            questionnaireId, processedAnswerData);

                    // 更新问卷结果记录
                    questionnaireResult.setResultData(resultDTO.getResultData());
                    questionnaireResult.setReport(resultDTO.getReport());
                    questionnaireResult.setScore(resultDTO.getScore());
                    questionnaireResult.setLevel(resultDTO.getLevel());

                    emoQuestionnaireResultMapper.updateById(questionnaireResult);

                    log.info("[submitQuestionnaireAnswer] 问卷结果生成成功，总分: {}, 评级: {}",
                            resultDTO.getScore(), resultDTO.getLevel());
                } else {
                    log.warn("[submitQuestionnaireAnswer] 问卷ID {} 暂不支持自动生成结果", questionnaireId);
                }
            } catch (Exception e) {
                log.error("[submitQuestionnaireAnswer] 生成问卷结果失败，但答案已保存: {}", e.getMessage(), e);
                // 不抛出异常，因为答案已经成功保存
            }

            return questionnaireResult.getId();

        } catch (Exception e) {
            log.error("[submitQuestionnaireAnswer] 提交问卷答案失败，错误信息: {}", e.getMessage(), e);

            // 根据不同的错误类型返回更友好的错误信息
            String errorMessage = e.getMessage();
            if (errorMessage.contains("解密") || errorMessage.contains("decrypt")) {
                throw new RuntimeException("数据解密失败，请检查传入的加密数据格式", e);
            } else if (errorMessage.contains("不存在")) {
                throw new RuntimeException("数据校验失败：" + errorMessage, e);
            } else if (errorMessage.contains("格式不正确")) {
                throw new RuntimeException("问卷答案数据格式不正确", e);
            } else {
                throw new RuntimeException("提交问卷答案失败: " + errorMessage, e);
            }
        }
    }

    @Override
    public List<AppBabyQuestionnaireResultRespVO> getBabyQuestionnaireResults(Long babyId) {
        // 根据 babyId 获取 assessmentId 列表并去重（查emo_assessment_result表）
        List<Long> assessmentIds = assessmentResultMapper.selectList(
                new LambdaQueryWrapperX<AssessmentResultDO>()
                        .eq(AssessmentResultDO::getBabyId, babyId)
                        .select(AssessmentResultDO::getAssessmentId)
        ).stream()
                .map(AssessmentResultDO::getAssessmentId)
                .distinct()
                .collect(Collectors.toList());

        if (assessmentIds.isEmpty()) {
            return new ArrayList<>();
        }

        System.out.println("[getBabyQuestionnaireResults] 找到测评ID列表: " + assessmentIds);

        // 获取测评标题(查emo_assessment表)
        Map<Long, String> assessmentTitleMap = assessmentMapper.selectBatchIds(assessmentIds)
                .stream()
                .collect(Collectors.toMap(
                        AssessmentDO::getId,
                        AssessmentDO::getTitle
                ));

        // 获取测评关联的问卷ID(查emo_assessment_questionnaire表)
        Map<Long, List<Long>> assessmentQuestionnaireMap = new HashMap<>();
        Set<Long> allQuestionnaireIds = new HashSet<>();
        for (Long assessmentId : assessmentIds) {
            List<AssessmentQuestionnaireDO> questionnaires =
                    assessmentQuestionnaireMapper.selectByAssessmentId(assessmentId);
            List<Long> questionnaireIds = questionnaires.stream()
                    .map(AssessmentQuestionnaireDO::getQuestionnaireId)
                    .collect(Collectors.toList());
            assessmentQuestionnaireMap.put(assessmentId, questionnaireIds);
            allQuestionnaireIds.addAll(questionnaireIds);
        }

        // 获取问卷标题(查emo_questionnaire表)
        Map<Long, String> questionnaireTitleMap = questionnaireMapper.selectBatchIds(new ArrayList<>(allQuestionnaireIds))
                .stream()
                .collect(Collectors.toMap(
                        QuestionnaireDO::getId,
                        QuestionnaireDO::getTitle
                ));

        // 获取每个(assessmentId, questionnaireId)的最新结果(查emo_questionnaire_result表)
        Map<String, EmoQuestionnaireResultDO> latestResultMap = new HashMap<>();
        for (Long assessmentId : assessmentIds) {
            List<Long> questionnaireIds = assessmentQuestionnaireMap.get(assessmentId);
            if (questionnaireIds != null) {
                for (Long questionnaireId : questionnaireIds) {
                    List<EmoQuestionnaireResultDO> results = emoQuestionnaireResultMapper.selectList(
                        new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                            .eq(EmoQuestionnaireResultDO::getBabyId, babyId)
                            .eq(EmoQuestionnaireResultDO::getAssessmentId, assessmentId)
                            .eq(EmoQuestionnaireResultDO::getQuestionnaireId, questionnaireId)
                            .isNotNull(EmoQuestionnaireResultDO::getCompletedTime)
                            .orderByDesc(EmoQuestionnaireResultDO::getCompletedTime)
                            .last("LIMIT 1")
                    );
                    if (!results.isEmpty()) {
                        latestResultMap.put(assessmentId + "_" + questionnaireId, results.get(0));
                    }
                }
            }
        }

        // 组装返回数据
        List<AppBabyQuestionnaireResultRespVO> resultList = new ArrayList<>();
        for (Long assessmentId : assessmentIds) {
            AppBabyQuestionnaireResultRespVO result = new AppBabyQuestionnaireResultRespVO();
            result.setAssessmentId(assessmentId);
            result.setAssessmentTitle(assessmentTitleMap.get(assessmentId));
            List<Long> questionnaireIds = assessmentQuestionnaireMap.get(assessmentId);
            result.setQuestionnaireCount(questionnaireIds != null ? questionnaireIds.size() : 0);

            // 构建问卷结果列表
            List<AppBabyQuestionnaireResultRespVO.QuestionnaireResultItem> questionnaireResults = new ArrayList<>();
            if (questionnaireIds != null) {
                for (Long questionnaireId : questionnaireIds) {
                    EmoQuestionnaireResultDO latestResult = latestResultMap.get(assessmentId + "_" + questionnaireId);
                    if (latestResult != null) {
                        AppBabyQuestionnaireResultRespVO.QuestionnaireResultItem item =
                                new AppBabyQuestionnaireResultRespVO.QuestionnaireResultItem();
                        item.setId(latestResult.getId());
                        item.setQuestionnaireId(questionnaireId);
                        item.setQuestionnaireTitle(questionnaireTitleMap.get(questionnaireId));
                        item.setCompletedTime(latestResult.getCompletedTime());
                        item.setScore(latestResult.getScore() != null ? latestResult.getScore().doubleValue() : null);
                        item.setLevel(latestResult.getLevel());
                        questionnaireResults.add(item);
                    }
                }
            }
            result.setQuestionnaireResults(questionnaireResults);
            
            if (!questionnaireResults.isEmpty()) {
                resultList.add(result);
            }
        }

        return resultList;
    }

    @Override
    public List<AppQuestionnaireResultListRespVO> getAllResultsByBabyAndQuestionnaire(Long babyId, Long questionnaireId, Long assessmentId) {
        // 查询所有结果
        LambdaQueryWrapperX<EmoQuestionnaireResultDO> wrapper = new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                .eqIfPresent(EmoQuestionnaireResultDO::getBabyId, babyId)
                .eqIfPresent(EmoQuestionnaireResultDO::getQuestionnaireId, questionnaireId)
                .eqIfPresent(EmoQuestionnaireResultDO::getAssessmentId, assessmentId)
                .orderByDesc(EmoQuestionnaireResultDO::getCompletedTime);
        List<EmoQuestionnaireResultDO> resultList = emoQuestionnaireResultMapper.selectList(wrapper);
        if (resultList.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        // 去重questionnaireId，查title
        java.util.Set<Long> questionnaireIds = resultList.stream()
                .map(EmoQuestionnaireResultDO::getQuestionnaireId)
                .collect(java.util.stream.Collectors.toSet());
        java.util.Map<Long, String> titleMap = questionnaireMapper.selectBatchIds(new java.util.ArrayList<>(questionnaireIds))
                .stream().collect(java.util.stream.Collectors.toMap(q -> q.getId(), q -> q.getTitle()));
        // 组装VO
        List<AppQuestionnaireResultListRespVO> voList = QuestionnaireResultConvert.INSTANCE.convertToListVOList(resultList);
        for (AppQuestionnaireResultListRespVO vo : voList) {
            vo.setTitle(titleMap.get(vo.getQuestionnaireId()));
        }
        return voList;
    }

    @Override
    public AppQuestionnaireResultVO getQuestionnaireResultById(Long id) {
        // 根据ID查询问卷结果
        EmoQuestionnaireResultDO questionnaireResult = emoQuestionnaireResultMapper.selectById(id);
        if (questionnaireResult == null) {
            return null;
        }
        
        // 转换为VO
        AppQuestionnaireResultVO vo = QuestionnaireResultConvert.INSTANCE.convertToAppVO(questionnaireResult);
        
        return vo;
    }

}

package cn.iocoder.yudao.module.emojump.service.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireAccessRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.convert.questionnaire.QuestionnaireConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireAccessDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireAccessMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentQuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentQuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentResultMapper;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentResultDO;
import cn.iocoder.yudao.module.emojump.enums.QuestionnaireStatusEnum;
import cn.iocoder.yudao.module.emojump.framework.survey.client.SurveySystemClient;
import cn.iocoder.yudao.module.emojump.framework.survey.util.SurveyDataConverter;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalSurveyUpdateReqVO;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalServiceResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.emojump.enums.ErrorCodeConstants.*;
import java.util.ArrayList;
import java.util.List;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireResultMapper;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireResultDO;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.BeanUtils;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.hutool.core.collection.CollUtil;

/**
 * 问卷管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Resource
    private QuestionnaireMapper questionnaireMapper;

    @Resource
    private QuestionnaireAccessMapper questionnaireAccessMapper;

    @Resource
    private AssessmentQuestionnaireMapper assessmentQuestionnaireMapper;

    @Resource
    private SurveySystemClient surveySystemClient;

    @Resource
    private QuestionnaireResultMapper questionnaireResultMapper;

    @Resource
    private AssessmentResultMapper assessmentResultMapper;

    @Override
    public Long createQuestionnaire(@Valid QuestionnaireCreateReqVO createReqVO) {
        // 插入
        QuestionnaireDO questionnaire = QuestionnaireConvert.INSTANCE.convert(createReqVO);
        questionnaire.setStatus(QuestionnaireStatusEnum.DRAFT.getStatus());
        questionnaire.setAccessCount(0);
        questionnaire.setCompletionCount(0);
        questionnaire.setIsOpen(createReqVO.getIsOpen() != null ? createReqVO.getIsOpen() : true);
        questionnaireMapper.insert(questionnaire);
        // 返回
        return questionnaire.getId();
    }

    @Override
    public void updateQuestionnaire(@Valid QuestionnaireUpdateReqVO updateReqVO) {
        // 校验存在
        QuestionnaireDO existingQuestionnaire = questionnaireMapper.selectById(updateReqVO.getId());
        if (existingQuestionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 检查是否需要更新外部问卷系统的时间配置
        boolean needUpdateExternalConfig = checkAndUpdateExternalConfig(existingQuestionnaire, updateReqVO);

        // 更新本地数据库
        QuestionnaireDO updateObj = QuestionnaireConvert.INSTANCE.convert(updateReqVO);
        questionnaireMapper.updateById(updateObj);

        if (needUpdateExternalConfig) {
            log.info("[updateQuestionnaire] 问卷时间配置已更新，ID: {}, 标题: {}",
                    updateReqVO.getId(), updateReqVO.getTitle());
        }
    }

    @Override
    public void deleteQuestionnaire(Long id) {
        // 校验存在
        validateQuestionnaireExists(id);
        // 删除
        questionnaireMapper.deleteById(id);
    }

    private void validateQuestionnaireExists(Long id) {
        if (questionnaireMapper.selectById(id) == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }
    }

    @Override
    public QuestionnaireRespVO getQuestionnaire(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        return QuestionnaireConvert.INSTANCE.convert(questionnaire);
    }

    @Override
    public PageResult<QuestionnaireRespVO> getQuestionnairePage(QuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public List<QuestionnaireRespVO> getAllQuestionnaireList() {
        List<QuestionnaireDO> questionnaireList = questionnaireMapper.selectList();
        return QuestionnaireConvert.INSTANCE.convertList(questionnaireList);
    }

    @Override
    @Transactional
    public void publishQuestionnaire(Long id) {
        // 校验存在
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 更新状态为已发布
        QuestionnaireDO updateObj = new QuestionnaireDO();
        updateObj.setId(id);
        updateObj.setStatus(QuestionnaireStatusEnum.PUBLISHED.getStatus());
        questionnaireMapper.updateById(updateObj);
    }

    @Override
    @Transactional
    public void unpublishQuestionnaire(Long id) {
        // 校验存在
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 更新状态为已下线
        QuestionnaireDO updateObj = new QuestionnaireDO();
        updateObj.setId(id);
        updateObj.setStatus(QuestionnaireStatusEnum.OFFLINE.getStatus());
        questionnaireMapper.updateById(updateObj);
    }

    @Override
    public PageResult<QuestionnaireRespVO> getPublishedQuestionnairePage(QuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPublishedPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public Boolean testQuestionnaireLink(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 这里可以添加实际的链接测试逻辑
        // 比如发送HTTP请求测试链接是否可访问
        // 暂时简单返回true
        return true;
    }

    // ==================== App端接口实现 ====================

    @Override
    public AppQuestionnaireRespVO getAppQuestionnaire(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }
        return QuestionnaireConvert.INSTANCE.convertApp(questionnaire);
    }

    // 状态常量
    private static final Integer ASSESSMENT_STATUS_IN_PROGRESS = 0;
    
    @Override
    public List<AppQuestionnaireRespVO> getPublishedAppQuestionnairePage(Long assessmentId, Long babyId) {
        System.out.println("[getPublishedAppQuestionnairePage] 入参: assessmentId=" + assessmentId + ", babyId=" + babyId);
        
        // 校验参数
        if (assessmentId == null && babyId == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 1. 查询关联表获取问卷列表并获取问卷ID
        List<AssessmentQuestionnaireDO> relations = assessmentQuestionnaireMapper.selectByAssessmentId(assessmentId);
        if (CollUtil.isEmpty(relations)) {
            return new ArrayList<>();
        }

        List<Long> questionnaireIds = relations.stream()
                .map(AssessmentQuestionnaireDO::getQuestionnaireId)
                .collect(Collectors.toList());
        System.out.println("[getPublishedAppQuestionnairePage] 关联问卷ID: " + questionnaireIds);

        // 2. 查询问卷列表
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        if (CollUtil.isEmpty(questionnaires)) {
            return new ArrayList<>();
        }

        // 3. 查询问卷结果
        List<QuestionnaireResultDO> results = questionnaireResultMapper.selectList(
                new LambdaQueryWrapper<QuestionnaireResultDO>()
                        .in(QuestionnaireResultDO::getQuestionnaireId, questionnaireIds)
                        .eq(assessmentId != null, QuestionnaireResultDO::getAssessmentId, assessmentId)
                        .eq(babyId != null, QuestionnaireResultDO::getBabyId, babyId)
        );

        // 4. 统一处理所有测评（按assessmentResultId分组过滤）
        Map<Long, List<QuestionnaireResultDO>> groupedByAssessmentResultId = results.stream()
                .filter(result -> result.getAssessmentResultId() != null)
                .collect(Collectors.groupingBy(QuestionnaireResultDO::getAssessmentResultId));

        System.out.println("[getPublishedAppQuestionnairePage] 统一处理逻辑，按assessmentResultId分组: " + groupedByAssessmentResultId.keySet());

        // 过滤掉已完成一轮测评的数据
        List<QuestionnaireResultDO> filteredResults = groupedByAssessmentResultId.entrySet().stream()
                .filter(entry -> {
                    Set<Long> groupQuestionnaireIds = entry.getValue().stream()
                            .map(QuestionnaireResultDO::getQuestionnaireId)
                            .collect(Collectors.toSet());
                    
                    boolean isIncomplete = groupQuestionnaireIds.size() < questionnaireIds.size();
                    if (!isIncomplete) {
                        System.out.println("[getPublishedAppQuestionnairePage] 过滤掉完整一轮的测评数据，assessmentResultId: " + entry.getKey());
                    }
                    return isIncomplete;
                })
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());

        // 5. 确定已完成的问卷ID集合
        Set<Long> completedQuestionnaireIds;
        if (filteredResults.isEmpty()) {
            // 如果过滤后数据为空，批量检查assessmentResultId的状态
            Set<Long> assessmentResultIds = groupedByAssessmentResultId.keySet();
            if (assessmentResultIds.isEmpty()) {
                completedQuestionnaireIds = new HashSet<>();
            } else {
                // 批量查询所有assessmentResultId的状态，提高性能
                List<AssessmentResultDO> assessmentResults = assessmentResultMapper.selectBatchIds(assessmentResultIds);
                boolean hasIncompleteAssessment = assessmentResults.stream()
                        .anyMatch(result -> result == null || result.getStatus() == null || ASSESSMENT_STATUS_IN_PROGRESS.equals(result.getStatus()));

                if (hasIncompleteAssessment) {
                    completedQuestionnaireIds = results.stream()
                            .map(QuestionnaireResultDO::getQuestionnaireId)
                            .collect(Collectors.toSet());
                    System.out.println("[getPublishedAppQuestionnairePage] 过滤后数据为空但存在进行中的测评结果，保持原有完成状态");
                } else {
                    completedQuestionnaireIds = new HashSet<>();
                    System.out.println("[getPublishedAppQuestionnairePage] 过滤后数据为空且所有测评结果都已完成，标记所有问卷为未完成");
                }
            }
        } else {
            completedQuestionnaireIds = filteredResults.stream()
                    .map(QuestionnaireResultDO::getQuestionnaireId)
                    .collect(Collectors.toSet());
            System.out.println("[getPublishedAppQuestionnairePage] 标记为已完成的问卷ID: " + completedQuestionnaireIds);
        }

        // 6. 构建返回结果
        List<AppQuestionnaireRespVO> appQuestionnaireList = QuestionnaireConvert.INSTANCE.convertAppList(questionnaires);
        appQuestionnaireList.forEach(appQuestionnaire -> 
                appQuestionnaire.setCompleted(completedQuestionnaireIds.contains(appQuestionnaire.getId())));

        return appQuestionnaireList;
    }

    @Override
    public AppQuestionnaireAccessRespVO getQuestionnaireAccess(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 创建访问响应
        AppQuestionnaireAccessRespVO accessRespVO = new AppQuestionnaireAccessRespVO();
        accessRespVO.setId(id);
        accessRespVO.setLink(questionnaire.getLink());
        accessRespVO.setAccessToken(generateAccessToken());
        accessRespVO.setExpireTime(LocalDateTime.now().plusHours(24)); // 24小时后过期

        return accessRespVO;
    }

    @Override
    @Transactional
    public void recordQuestionnaireAccess(Long id, Long babyId) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 增加访问次数
        questionnaireMapper.updateById(new QuestionnaireDO().setId(id).setAccessCount(questionnaire.getAccessCount() + 1));

        // 记录访问日志
        QuestionnaireAccessDO accessLog = new QuestionnaireAccessDO();
        accessLog.setQuestionnaireId(id);
        accessLog.setBabyId(babyId);
        accessLog.setIpAddress(ServletUtils.getClientIP());
        accessLog.setUserAgent(ServletUtils.getUserAgent());
        accessLog.setAccessTime(LocalDateTime.now());
        questionnaireAccessMapper.insert(accessLog);
    }

    @Override
    public PageResult<AppQuestionnaireRespVO> getPopularQuestionnairePage(AppQuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPopularPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertAppPage(pageResult);
    }

    @Override
    public PageResult<AppQuestionnaireRespVO> searchQuestionnaire(String keyword, AppQuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.searchQuestionnaire(keyword, pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertAppPage(pageResult);
    }

    private String generateAccessToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 检查并更新外部问卷系统配置
     *
     * @param existingQuestionnaire 现有问卷数据
     * @param updateReqVO 更新请求
     * @return 是否需要更新外部配置
     */
    private boolean checkAndUpdateExternalConfig(QuestionnaireDO existingQuestionnaire, QuestionnaireUpdateReqVO updateReqVO) {
        // 检查是否是外部同步的问卷
        if (!isExternalSyncQuestionnaire(existingQuestionnaire)) {
            return false;
        }

        // 检查validFrom和validTo是否发生变化
        boolean validFromChanged = !Objects.equals(existingQuestionnaire.getValidFrom(), updateReqVO.getValidFrom());
        boolean validToChanged = !Objects.equals(existingQuestionnaire.getValidTo(), updateReqVO.getValidTo());

        if (!validFromChanged && !validToChanged) {
            return false; // 时间没有变化，不需要更新外部配置
        }

        // 获取外部问卷ID
        String externalSurveyId = extractExternalSurveyId(existingQuestionnaire.getRemark());
        if (!StringUtils.hasText(externalSurveyId)) {
            log.warn("[checkAndUpdateExternalConfig] 无法获取外部问卷ID，问卷ID: {}, remark: {}",
                    existingQuestionnaire.getId(), existingQuestionnaire.getRemark());
            return false;
        }

        // 构建更新请求
        ExternalSurveyUpdateReqVO externalUpdateReq = new ExternalSurveyUpdateReqVO();
        externalUpdateReq.setSurveyId(externalSurveyId);
        externalUpdateReq.setBeginTime(SurveyDataConverter.formatTimeForExternal(updateReqVO.getValidFrom()));
        externalUpdateReq.setEndTime(SurveyDataConverter.formatTimeForExternal(updateReqVO.getValidTo()));

        log.info("[checkAndUpdateExternalConfig] 准备更新外部问卷配置，问卷ID: {}, 外部ID: {}, 开始时间: {} -> {}, 结束时间: {} -> {}",
                existingQuestionnaire.getId(), externalSurveyId,
                SurveyDataConverter.formatTimeForExternal(existingQuestionnaire.getValidFrom()),
                externalUpdateReq.getBeginTime(),
                SurveyDataConverter.formatTimeForExternal(existingQuestionnaire.getValidTo()),
                externalUpdateReq.getEndTime());

        // 调用外部接口更新配置
        boolean updateSuccess = surveySystemClient.updateSurveySimpleConfig(externalUpdateReq);

        if (updateSuccess) {
            log.info("[checkAndUpdateExternalConfig] 外部问卷配置更新成功，问卷ID: {}, 外部ID: {}",
                    existingQuestionnaire.getId(), externalSurveyId);
        } else {
            log.error("[checkAndUpdateExternalConfig] 外部问卷配置更新失败，问卷ID: {}, 外部ID: {}",
                    existingQuestionnaire.getId(), externalSurveyId);
        }

        return updateSuccess;
    }

    /**
     * 判断是否是外部同步的问卷
     *
     * @param questionnaire 问卷数据
     * @return 是否是外部同步的问卷
     */
    private boolean isExternalSyncQuestionnaire(QuestionnaireDO questionnaire) {
        return questionnaire != null &&
               StringUtils.hasText(questionnaire.getRemark()) &&
               questionnaire.getRemark().startsWith("external_id:");
    }

    /**
     * 从备注中提取外部问卷ID
     *
     * @param remark 备注信息，格式为 "external_id:外部ID"
     * @return 外部问卷ID，如果提取失败返回null
     */
    private String extractExternalSurveyId(String remark) {
        if (!StringUtils.hasText(remark) || !remark.startsWith("external_id:")) {
            return null;
        }

        try {
            return remark.substring("external_id:".length());
        } catch (Exception e) {
            log.warn("[extractExternalSurveyId] 提取外部问卷ID失败，remark: {}", remark, e);
            return null;
        }
    }

    @Override
    public ExternalServiceResult publishQuestionnaireToExternal(Long id) {
        // 校验问卷存在
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            return ExternalServiceResult.error("问卷不存在");
        }

        // 检查是否是外部同步的问卷
        if (!isExternalSyncQuestionnaire(questionnaire)) {
            return ExternalServiceResult.error("该问卷不是从外部系统同步的问卷，无法执行发布操作");
        }

        // 获取外部问卷ID
        String externalSurveyId = extractExternalSurveyId(questionnaire.getRemark());
        if (!StringUtils.hasText(externalSurveyId)) {
            log.warn("[publishQuestionnaireToExternal] 无法获取外部问卷ID，问卷ID: {}, remark: {}",
                    questionnaire.getId(), questionnaire.getRemark());
            return ExternalServiceResult.error("无法获取外部问卷ID");
        }

        log.info("[publishQuestionnaireToExternal] 准备发布问卷到外部系统，问卷ID: {}, 外部ID: {}, 标题: {}",
                questionnaire.getId(), externalSurveyId, questionnaire.getTitle());

        // 调用外部接口发布问卷
        ExternalServiceResult result = surveySystemClient.publishSurvey(externalSurveyId);

        if (result.isSuccess()) {
            // 更新本地问卷状态为已发布
            questionnaire.setStatus(QuestionnaireStatusEnum.PUBLISHED.getStatus());
            questionnaire.setIsOpen(true);
            questionnaire.setUpdateTime(LocalDateTime.now());
            questionnaire.setUpdater("system_publish");
            questionnaireMapper.updateById(questionnaire);

            log.info("[publishQuestionnaireToExternal] 问卷发布成功，问卷ID: {}, 外部ID: {}",
                    questionnaire.getId(), externalSurveyId);
        } else {
            log.error("[publishQuestionnaireToExternal] 问卷发布失败，问卷ID: {}, 外部ID: {}, 错误: {}",
                    questionnaire.getId(), externalSurveyId, result.getErrorMessage());
        }

        return result;
    }

    @Override
    public ExternalServiceResult pauseQuestionnaireInExternal(Long id) {
        // 校验问卷存在
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            return ExternalServiceResult.error("问卷不存在");
        }

        // 检查是否是外部同步的问卷
        if (!isExternalSyncQuestionnaire(questionnaire)) {
            return ExternalServiceResult.error("该问卷不是从外部系统同步的问卷，无法执行暂停操作");
        }

        // 获取外部问卷ID
        String externalSurveyId = extractExternalSurveyId(questionnaire.getRemark());
        if (!StringUtils.hasText(externalSurveyId)) {
            log.warn("[pauseQuestionnaireInExternal] 无法获取外部问卷ID，问卷ID: {}, remark: {}",
                    questionnaire.getId(), questionnaire.getRemark());
            return ExternalServiceResult.error("无法获取外部问卷ID");
        }

        log.info("[pauseQuestionnaireInExternal] 准备暂停外部系统问卷，问卷ID: {}, 外部ID: {}, 标题: {}",
                questionnaire.getId(), externalSurveyId, questionnaire.getTitle());

        // 调用外部接口暂停问卷
        ExternalServiceResult result = surveySystemClient.pauseSurvey(externalSurveyId);

        if (result.isSuccess()) {
            // 更新本地问卷状态为已下线
            questionnaire.setStatus(QuestionnaireStatusEnum.OFFLINE.getStatus());
            questionnaire.setIsOpen(false);
            questionnaire.setUpdateTime(LocalDateTime.now());
            questionnaire.setUpdater("system_pause");
            questionnaireMapper.updateById(questionnaire);

            log.info("[pauseQuestionnaireInExternal] 问卷暂停成功，问卷ID: {}, 外部ID: {}",
                    questionnaire.getId(), externalSurveyId);
        } else {
            log.error("[pauseQuestionnaireInExternal] 问卷暂停失败，问卷ID: {}, 外部ID: {}, 错误: {}",
                    questionnaire.getId(), externalSurveyId, result.getErrorMessage());
        }

        return result;
    }
}
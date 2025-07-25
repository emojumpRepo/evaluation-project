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
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.convert.questionnaire.QuestionnaireConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireAccessDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireAccessMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentQuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentQuestionnaireDO;
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

    @Override
    public PageResult<AppQuestionnaireRespVO> getPublishedAppQuestionnairePage(AppQuestionnairePageReqVO pageReqVO) {
        System.out.println("[getPublishedAppQuestionnairePage] 入参: " + pageReqVO);
        // 新增：如果传入 assessmentId，则按测评-问卷关联表查找
        if (pageReqVO.getAssessmentId() != null) {
            Long assessmentId = pageReqVO.getAssessmentId();
            System.out.println("[getPublishedAppQuestionnairePage] assessmentId: " + assessmentId);
            // 1. 查询关联表获取 questionnaireId 列表
            List<AssessmentQuestionnaireDO> relations = assessmentQuestionnaireMapper.selectByAssessmentId(assessmentId);
            if (relations == null || relations.isEmpty()) {
                // 返回空分页
                return new PageResult<>(new ArrayList<>(), 0L);
            }
            List<Long> questionnaireIds = new ArrayList<>();
            for (AssessmentQuestionnaireDO relation : relations) {
                questionnaireIds.add(relation.getQuestionnaireId());
            }
            System.out.println("[getPublishedAppQuestionnairePage] 关联问卷ID: " + questionnaireIds);
            // 2. 分页查询问卷表（只查当前页）
            PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPage(pageReqVO, questionnaireIds);
            List<QuestionnaireDO> questionnaires = pageResult.getList();
            // 3. 查询问卷完成情况（只查当前页的问卷）
            List<Long> pageQuestionnaireIds = new ArrayList<>();
            for (QuestionnaireDO q : questionnaires) {
                pageQuestionnaireIds.add(q.getId());
            }
            List<QuestionnaireResultDO> resultList = pageQuestionnaireIds.isEmpty() ? new ArrayList<>() : questionnaireResultMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<QuestionnaireResultDO>()
                    .in(QuestionnaireResultDO::getQuestionnaireId, pageQuestionnaireIds)
            );
            // 4. 构建questionnaireId到completed的映射
            java.util.Set<Long> completedSet = new java.util.HashSet<>();
            for (QuestionnaireResultDO result : resultList) {
                completedSet.add(result.getQuestionnaireId());
            }
            // 5. 转换为 AppQuestionnaireRespVO，并设置completed字段
            List<AppQuestionnaireRespVO> voList = QuestionnaireConvert.INSTANCE.convertAppList(questionnaires);
            for (AppQuestionnaireRespVO vo : voList) {
                vo.setCompleted(completedSet.contains(vo.getId()));
            }
            // 6. 构造分页对象（分页返回）
            return new PageResult<>(voList, pageResult.getTotal());
        } else {
            // 原有逻辑：分页查找所有已发布问卷
            PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPublishedPage(pageReqVO);
            return QuestionnaireConvert.INSTANCE.convertAppPage(pageResult);
        }
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
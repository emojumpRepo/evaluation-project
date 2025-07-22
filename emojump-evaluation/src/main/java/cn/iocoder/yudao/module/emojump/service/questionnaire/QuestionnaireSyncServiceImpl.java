package cn.iocoder.yudao.module.emojump.service.questionnaire;

import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.enums.QuestionnaireStatusEnum;
import cn.iocoder.yudao.module.emojump.framework.config.SurveySystemProperties;
import cn.iocoder.yudao.module.emojump.framework.survey.client.SurveySystemClient;
import cn.iocoder.yudao.module.emojump.framework.survey.util.SurveyDataConverter;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalSurveyRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 问卷同步服务实现类
 *
 * @author 芋道源码
 */
@Slf4j
@Service
public class QuestionnaireSyncServiceImpl implements QuestionnaireSyncService {

    @Resource
    private SurveySystemClient surveySystemClient;

    @Resource
    private QuestionnaireMapper questionnaireMapper;

    @Resource
    private SurveySystemProperties surveySystemProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionnaireSyncResult syncQuestionnaires() {
        log.info("[syncQuestionnaires] 开始同步外部问卷系统数据");
        
        QuestionnaireSyncResult result = new QuestionnaireSyncResult();
        
        try {
            // 1. 获取外部问卷系统的问卷列表
            List<ExternalSurveyRespVO> externalSurveys = surveySystemClient.getSurveyListWithRetry();
            
            if (CollectionUtils.isEmpty(externalSurveys)) {
                log.warn("[syncQuestionnaires] 外部问卷系统返回空列表");
                result.setSuccess(true);
                return result;
            }

            // 2. 获取本地所有问卷数据
            List<QuestionnaireDO> localQuestionnaires = questionnaireMapper.selectList();
            
            // 3. 构建本地问卷的外部ID映射
            Map<String, QuestionnaireDO> localQuestionnaireMap = new HashMap<>();
            for (QuestionnaireDO questionnaire : localQuestionnaires) {
                if (StringUtils.hasText(questionnaire.getRemark()) && 
                    questionnaire.getRemark().startsWith("external_id:")) {
                    String externalId = questionnaire.getRemark().substring("external_id:".length());
                    localQuestionnaireMap.put(externalId, questionnaire);
                }
            }

            // 4. 获取外部问卷的ID集合
            Set<String> externalIds = externalSurveys.stream()
                    .map(ExternalSurveyRespVO::getSurveyMetaId)
                    .collect(Collectors.toSet());

            // 5. 处理外部问卷数据
            for (ExternalSurveyRespVO externalSurvey : externalSurveys) {
                QuestionnaireDO localQuestionnaire = localQuestionnaireMap.get(externalSurvey.getSurveyMetaId());
                
                if (localQuestionnaire == null) {
                    // 新增问卷
                    createNewQuestionnaire(externalSurvey);
                    result.setNewAdded(result.getNewAdded() + 1);
                } else {
                    // 更新现有问卷
                    if (updateExistingQuestionnaire(localQuestionnaire, externalSurvey)) {
                        result.setUpdated(result.getUpdated() + 1);
                    }
                }
            }

            // 6. 标记本地存在但外部不存在的问卷为失效状态
            for (QuestionnaireDO localQuestionnaire : localQuestionnaires) {
                if (StringUtils.hasText(localQuestionnaire.getRemark()) && 
                    localQuestionnaire.getRemark().startsWith("external_id:")) {
                    String externalId = localQuestionnaire.getRemark().substring("external_id:".length());
                    
                    if (!externalIds.contains(externalId) && 
                        !QuestionnaireStatusEnum.INVALID.getStatus().equals(localQuestionnaire.getStatus())) {
                        // 标记为失效
                        localQuestionnaire.setStatus(QuestionnaireStatusEnum.INVALID.getStatus());
                        localQuestionnaire.setUpdateTime(LocalDateTime.now());
                        questionnaireMapper.updateById(localQuestionnaire);
                        result.setInvalidated(result.getInvalidated() + 1);
                        
                        log.info("[syncQuestionnaires] 标记问卷为失效状态，ID: {}, 标题: {}", 
                                localQuestionnaire.getId(), localQuestionnaire.getTitle());
                    }
                }
            }

            result.setTotalProcessed(externalSurveys.size());
            result.setSuccess(true);
            
            log.info("[syncQuestionnaires] 同步完成，结果: {}", result);
            
        } catch (Exception e) {
            log.error("[syncQuestionnaires] 同步过程中发生异常", e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
        }
        
        return result;
    }

    @Override
    public QuestionnaireSyncResult manualSync() {
        log.info("[manualSync] 手动触发问卷同步");
        return syncQuestionnaires();
    }

    /**
     * 创建新问卷
     */
    private void createNewQuestionnaire(ExternalSurveyRespVO externalSurvey) {
        QuestionnaireDO questionnaire = new QuestionnaireDO();

        // 设置必填字段，提供默认值防止null
        questionnaire.setTitle(StringUtils.hasText(externalSurvey.getTitle()) ?
                externalSurvey.getTitle() : "未命名问卷");
        questionnaire.setDescription(SurveyDataConverter.generateDescription(externalSurvey));
        questionnaire.setLink(SurveyDataConverter.generateSurveyLink(externalSurvey, surveySystemProperties.getBaseUrl()));

        // 设置其他字段，使用转换工具类
        questionnaire.setType(SurveyDataConverter.convertSurveyType(externalSurvey.getSurveyType()));
        questionnaire.setStatus(SurveyDataConverter.convertStatus(externalSurvey));
        questionnaire.setTargetAudience(SurveyDataConverter.generateTargetAudience(externalSurvey));
        questionnaire.setEstimatedDuration(SurveyDataConverter.estimateDuration(externalSurvey.getSurveyType()));
        questionnaire.setAccessCount(0);
        questionnaire.setCompletionCount(externalSurvey.getSubmitCount() != null ? externalSurvey.getSubmitCount() : 0);
        questionnaire.setIsOpen(SurveyDataConverter.isOpen(externalSurvey));
        questionnaire.setValidFrom(SurveyDataConverter.parseTime(externalSurvey.getBeginTime()));
        questionnaire.setValidTo(SurveyDataConverter.parseTime(externalSurvey.getEndTime()));
        questionnaire.setRemark("external_id:" + externalSurvey.getSurveyMetaId());
        questionnaire.setCreator("system_sync");
        questionnaire.setCreateTime(LocalDateTime.now());
        questionnaire.setUpdateTime(LocalDateTime.now());

        questionnaireMapper.insert(questionnaire);

        log.info("[createNewQuestionnaire] 新增问卷，外部ID: {}, 标题: {}, 状态: {}, 类型: {}, 完成次数: {}",
                externalSurvey.getSurveyMetaId(), questionnaire.getTitle(),
                externalSurvey.getCurrentStatus(), externalSurvey.getSurveyType(),
                externalSurvey.getSubmitCount());
    }

    /**
     * 更新现有问卷
     */
    private boolean updateExistingQuestionnaire(QuestionnaireDO localQuestionnaire, ExternalSurveyRespVO externalSurvey) {
        boolean needUpdate = false;

        // 生成新的字段值
        String newTitle = StringUtils.hasText(externalSurvey.getTitle()) ? externalSurvey.getTitle() : "未命名问卷";
//        String newDescription = SurveyDataConverter.generateDescription(externalSurvey);
        String newLink = SurveyDataConverter.generateSurveyLink(externalSurvey, surveySystemProperties.getBaseUrl());
//        Integer newType = SurveyDataConverter.convertSurveyType(externalSurvey.getSurveyType());
        Integer newStatus = SurveyDataConverter.convertStatus(externalSurvey);
//        String newTargetAudience = SurveyDataConverter.generateTargetAudience(externalSurvey);
//        Integer newEstimatedDuration = SurveyDataConverter.estimateDuration(externalSurvey.getSurveyType());
        Integer newCompletionCount = externalSurvey.getSubmitCount() != null ? externalSurvey.getSubmitCount() : 0;
        Boolean newIsOpen = SurveyDataConverter.isOpen(externalSurvey);

        // 检查是否需要更新
        if (!Objects.equals(localQuestionnaire.getTitle(), newTitle) ||
//            !Objects.equals(localQuestionnaire.getDescription(), newDescription) ||
            !Objects.equals(localQuestionnaire.getLink(), newLink) ||
//            !Objects.equals(localQuestionnaire.getType(), newType) ||
            !Objects.equals(localQuestionnaire.getStatus(), newStatus) ||
//            !Objects.equals(localQuestionnaire.getTargetAudience(), newTargetAudience) ||
//            !Objects.equals(localQuestionnaire.getEstimatedDuration(), newEstimatedDuration) ||
            !Objects.equals(localQuestionnaire.getCompletionCount(), newCompletionCount) ||
            !Objects.equals(localQuestionnaire.getIsOpen(), newIsOpen)) {

            needUpdate = true;
        }

        if (needUpdate) {
            // 更新字段
            localQuestionnaire.setTitle(newTitle);
//            localQuestionnaire.setDescription(newDescription);
            localQuestionnaire.setLink(newLink);
//            localQuestionnaire.setType(newType);
            localQuestionnaire.setStatus(newStatus);
//            localQuestionnaire.setTargetAudience(newTargetAudience);
//            localQuestionnaire.setEstimatedDuration(newEstimatedDuration);
            localQuestionnaire.setCompletionCount(newCompletionCount);
            localQuestionnaire.setIsOpen(newIsOpen);
            localQuestionnaire.setValidFrom(SurveyDataConverter.parseTime(externalSurvey.getBeginTime()));
            localQuestionnaire.setValidTo(SurveyDataConverter.parseTime(externalSurvey.getEndTime()));
            localQuestionnaire.setUpdater("system_sync");
            localQuestionnaire.setUpdateTime(LocalDateTime.now());

            questionnaireMapper.updateById(localQuestionnaire);

            log.info("[updateExistingQuestionnaire] 更新问卷，ID: {}, 标题: {}, 外部状态: {} -> 本地状态: {}, 完成次数: {} -> {}",
                    localQuestionnaire.getId(), localQuestionnaire.getTitle(),
                    externalSurvey.getCurrentStatus(), newStatus,
                    localQuestionnaire.getCompletionCount(), newCompletionCount);
        }

        return needUpdate;
    }

}

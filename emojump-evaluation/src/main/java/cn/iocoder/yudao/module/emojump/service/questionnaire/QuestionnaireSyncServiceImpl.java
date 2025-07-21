package cn.iocoder.yudao.module.emojump.service.questionnaire;

import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.enums.QuestionnaireStatusEnum;
import cn.iocoder.yudao.module.emojump.framework.survey.client.SurveySystemClient;
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
                    .map(ExternalSurveyRespVO::getExternalId)
                    .collect(Collectors.toSet());

            // 5. 处理外部问卷数据
            for (ExternalSurveyRespVO externalSurvey : externalSurveys) {
                QuestionnaireDO localQuestionnaire = localQuestionnaireMap.get(externalSurvey.getExternalId());
                
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
        QuestionnaireDO questionnaire = QuestionnaireDO.builder()
                .title(externalSurvey.getTitle())
                .description(externalSurvey.getDescription())
                .link(externalSurvey.getLink())
                .type(externalSurvey.getType())
                .status(externalSurvey.getStatus())
                .targetAudience(externalSurvey.getTargetAudience())
                .estimatedDuration(externalSurvey.getEstimatedDuration())
                .accessCount(0)
                .completionCount(0)
                .isOpen(externalSurvey.getIsOpen())
                .validFrom(externalSurvey.getValidFrom())
                .validTo(externalSurvey.getValidTo())
                .remark("external_id:" + externalSurvey.getExternalId())
                .creator("system_sync")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        questionnaireMapper.insert(questionnaire);
        
        log.info("[createNewQuestionnaire] 新增问卷，外部ID: {}, 标题: {}", 
                externalSurvey.getExternalId(), externalSurvey.getTitle());
    }

    /**
     * 更新现有问卷
     */
    private boolean updateExistingQuestionnaire(QuestionnaireDO localQuestionnaire, ExternalSurveyRespVO externalSurvey) {
        boolean needUpdate = false;

        // 检查是否需要更新
        if (!localQuestionnaire.getTitle().equals(externalSurvey.getTitle()) ||
            !localQuestionnaire.getDescription().equals(externalSurvey.getDescription()) ||
            !localQuestionnaire.getLink().equals(externalSurvey.getLink()) ||
            !localQuestionnaire.getType().equals(externalSurvey.getType()) ||
            !localQuestionnaire.getStatus().equals(externalSurvey.getStatus()) ||
            !localQuestionnaire.getTargetAudience().equals(externalSurvey.getTargetAudience()) ||
            !localQuestionnaire.getEstimatedDuration().equals(externalSurvey.getEstimatedDuration()) ||
            !localQuestionnaire.getIsOpen().equals(externalSurvey.getIsOpen())) {
            
            needUpdate = true;
        }

        if (needUpdate) {
            localQuestionnaire.setTitle(externalSurvey.getTitle());
            localQuestionnaire.setDescription(externalSurvey.getDescription());
            localQuestionnaire.setLink(externalSurvey.getLink());
            localQuestionnaire.setType(externalSurvey.getType());
            localQuestionnaire.setStatus(externalSurvey.getStatus());
            localQuestionnaire.setTargetAudience(externalSurvey.getTargetAudience());
            localQuestionnaire.setEstimatedDuration(externalSurvey.getEstimatedDuration());
            localQuestionnaire.setIsOpen(externalSurvey.getIsOpen());
            localQuestionnaire.setValidFrom(externalSurvey.getValidFrom());
            localQuestionnaire.setValidTo(externalSurvey.getValidTo());
            localQuestionnaire.setUpdater("system_sync");
            localQuestionnaire.setUpdateTime(LocalDateTime.now());

            questionnaireMapper.updateById(localQuestionnaire);
            
            log.info("[updateExistingQuestionnaire] 更新问卷，ID: {}, 标题: {}", 
                    localQuestionnaire.getId(), localQuestionnaire.getTitle());
        }

        return needUpdate;
    }

}

package cn.iocoder.yudao.module.emojump.service.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.*;
import cn.iocoder.yudao.module.emojump.convert.assessment.AssessmentConvert;
import cn.iocoder.yudao.module.emojump.convert.questionnaire.QuestionnaireConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentQuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentQuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.enums.AssessmentStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emojump.enums.ErrorCodeConstants.*;

/**
 * 测评管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AssessmentServiceImpl implements AssessmentService {

    @Resource
    private AssessmentMapper assessmentMapper;

    @Resource
    private QuestionnaireMapper questionnaireMapper;

    @Resource
    private AssessmentQuestionnaireMapper assessmentQuestionnaireMapper;

    @Override
    @Transactional
    public Long createAssessment(@Valid AssessmentCreateReqVO createReqVO) {
        // 校验问卷存在
        validateQuestionnairesExist(createReqVO.getQuestionnaires());

        // 插入测评
        AssessmentDO assessment = AssessmentConvert.INSTANCE.convert(createReqVO);
        assessment.setStatus(AssessmentStatusEnum.DRAFT.getStatus());
        assessment.setCurrentParticipants(0);
        assessmentMapper.insert(assessment);

        // 插入测评问卷关联
        createAssessmentQuestionnaires(assessment.getId(), createReqVO.getQuestionnaires());

        // 返回
        return assessment.getId();
    }

    @Override
    @Transactional
    public void updateAssessment(@Valid AssessmentUpdateReqVO updateReqVO) {
        // 校验存在
        validateAssessmentExists(updateReqVO.getId());

        // 校验问卷存在
        validateQuestionnairesExist(updateReqVO.getQuestionnaires());

        // 更新测评
        AssessmentDO updateObj = AssessmentConvert.INSTANCE.convert(updateReqVO);
        assessmentMapper.updateById(updateObj);

        // 更新测评问卷关联
        assessmentQuestionnaireMapper.deleteByAssessmentId(updateReqVO.getId());
        createAssessmentQuestionnaires(updateReqVO.getId(), updateReqVO.getQuestionnaires());
    }

    @Override
    public void deleteAssessment(Long id) {
        // 校验存在
        validateAssessmentExists(id);
        // 删除
        assessmentMapper.deleteById(id);
    }

    private void validateAssessmentExists(Long id) {
        if (assessmentMapper.selectById(id) == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }
    }

    private void validateQuestionnaireExists(Long id) {
        if (questionnaireMapper.selectById(id) == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }
    }

    @Override
    public AssessmentRespVO getAssessment(Long id) {
        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }

        AssessmentRespVO respVO = AssessmentConvert.INSTANCE.convert(assessment);

        // 填充问卷信息
        fillAssessmentQuestionnaires(respVO);

        return respVO;
    }

    @Override
    public PageResult<AssessmentRespVO> getAssessmentPage(AssessmentPageReqVO pageReqVO) {
        PageResult<AssessmentDO> pageResult = assessmentMapper.selectPage(pageReqVO);
        PageResult<AssessmentRespVO> convertPage = AssessmentConvert.INSTANCE.convertPage(pageResult);

        // 填充问卷信息
        convertPage.getList().forEach(this::fillAssessmentQuestionnaires);

        return convertPage;
    }

    @Override
    @Transactional
    public void publishAssessment(Long id) {
        // 校验存在
        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }
        
        // 更新状态为已发布
        AssessmentDO updateObj = new AssessmentDO();
        updateObj.setId(id);
        updateObj.setStatus(AssessmentStatusEnum.PUBLISHED.getStatus());
        assessmentMapper.updateById(updateObj);
    }

    @Override
    @Transactional
    public void unpublishAssessment(Long id) {
        // 校验存在
        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }
        
        // 更新状态为已结束
        AssessmentDO updateObj = new AssessmentDO();
        updateObj.setId(id);
        updateObj.setStatus(AssessmentStatusEnum.ENDED.getStatus());
        assessmentMapper.updateById(updateObj);
    }

    @Override
    public PageResult<QuestionnaireRespVO> getAvailableQuestionnaires(QuestionnairePageReqVO pageReqVO) {
        // 获取已发布的问卷
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPublishedPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertPage(pageResult);
    }

    // ==================== App端接口实现 ====================

    @Override
    public AppAssessmentRespVO getAppAssessment(Long id) {
        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }

        AppAssessmentRespVO respVO = AssessmentConvert.INSTANCE.convertApp(assessment);

        // 填充问卷信息
        fillAppAssessmentQuestionnaires(respVO);

        // TODO: 检查用户是否已参与（需要用户系统支持）
        respVO.setIsParticipated(false);

        return respVO;
    }

    @Override
    public PageResult<AppAssessmentRespVO> getAppAssessmentPage(AppAssessmentPageReqVO pageReqVO) {
        PageResult<AssessmentDO> pageResult = assessmentMapper.selectPage(pageReqVO);
        PageResult<AppAssessmentRespVO> convertPage = AssessmentConvert.INSTANCE.convertAppPage(pageResult);

        // 填充问卷信息
        for (AppAssessmentRespVO assessmentVO : convertPage.getList()) {
            fillAppAssessmentQuestionnaires(assessmentVO);
            // TODO: 检查用户是否已参与（需要用户系统支持）
            assessmentVO.setIsParticipated(false);
        }

        return convertPage;
    }

    @Override
    public PageResult<AppAssessmentRespVO> getPublishedAssessmentPage(AppAssessmentPageReqVO pageReqVO) {
        PageResult<AssessmentDO> pageResult = assessmentMapper.selectPublishedPage(pageReqVO);
        PageResult<AppAssessmentRespVO> convertPage = AssessmentConvert.INSTANCE.convertAppPage(pageResult);

        // 填充问卷信息
        for (AppAssessmentRespVO assessmentVO : convertPage.getList()) {
            fillAppAssessmentQuestionnaires(assessmentVO);
            // TODO: 检查用户是否已参与（需要用户系统支持）
            assessmentVO.setIsParticipated(false);
        }

        return convertPage;
    }

    @Override
    public AppAssessmentParticipateRespVO participateAssessment(Long id) {
        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }
        
        // 检查测评状态
        if (!AssessmentStatusEnum.PUBLISHED.getStatus().equals(assessment.getStatus())) {
            throw exception(ASSESSMENT_NOT_PUBLISHED);
        }
        
        // 检查是否过期
        if (assessment.getEndTime() != null && assessment.getEndTime().isBefore(LocalDateTime.now())) {
            throw exception(ASSESSMENT_EXPIRED);
        }
        
        // 检查参与人数
        if (assessment.getMaxParticipants() != null && 
            assessment.getCurrentParticipants() >= assessment.getMaxParticipants()) {
            throw exception(ASSESSMENT_FULL);
        }
        
        // 获取测评关联的问卷信息
        List<AssessmentQuestionnaireDO> assessmentQuestionnaires = assessmentQuestionnaireMapper.selectByAssessmentId(id);
        if (assessmentQuestionnaires.isEmpty()) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 获取问卷详细信息
        List<Long> questionnaireIds = assessmentQuestionnaires.stream()
                .map(AssessmentQuestionnaireDO::getQuestionnaireId)
                .collect(Collectors.toList());
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        Map<Long, QuestionnaireDO> questionnaireMap = questionnaires.stream()
                .collect(Collectors.toMap(QuestionnaireDO::getId, q -> q));

        // 构建问卷参与信息
        List<AppAssessmentParticipateRespVO.ParticipateQuestionnaireVO> participateQuestionnaires =
                assessmentQuestionnaires.stream()
                .map(aq -> {
                    QuestionnaireDO questionnaire = questionnaireMap.get(aq.getQuestionnaireId());
                    AppAssessmentParticipateRespVO.ParticipateQuestionnaireVO vo =
                            new AppAssessmentParticipateRespVO.ParticipateQuestionnaireVO();
                    vo.setQuestionnaireId(aq.getQuestionnaireId());
                    vo.setSortOrder(aq.getSortOrder());
                    vo.setIsRequired(aq.getIsRequired());
                    if (questionnaire != null) {
                        vo.setQuestionnaireTitle(questionnaire.getTitle());
                        vo.setQuestionnaireLink(questionnaire.getLink());
                        vo.setEstimatedDuration(questionnaire.getEstimatedDuration());
                    }
                    return vo;
                })
                .collect(Collectors.toList());

        // 创建参与响应
        AppAssessmentParticipateRespVO respVO = new AppAssessmentParticipateRespVO();
        respVO.setAssessmentId(id);
        respVO.setAssessmentTitle(assessment.getTitle());
        respVO.setQuestionnaires(participateQuestionnaires);
        respVO.setAccessToken(generateAccessToken());

        // TODO: 记录用户参与（需要用户系统支持）

        return respVO;
    }

    @Override
    @Transactional
    public void submitAssessment(AppAssessmentSubmitReqVO submitReqVO) {
        AssessmentDO assessment = assessmentMapper.selectById(submitReqVO.getAssessmentId());
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }
        
        // TODO: 保存测评结果（需要创建测评结果表）
        // TODO: 增加参与人数
        
        // 更新参与人数
        AssessmentDO updateObj = new AssessmentDO();
        updateObj.setId(submitReqVO.getAssessmentId());
        updateObj.setCurrentParticipants(assessment.getCurrentParticipants() + 1);
        assessmentMapper.updateById(updateObj);
    }

    @Override
    public PageResult<AppAssessmentRespVO> getMyAssessmentPage(AppAssessmentPageReqVO pageReqVO) {
        // TODO: 根据当前用户获取我的测评列表（需要用户系统支持）
        // 暂时返回空列表
        return new PageResult<>(null, 0L);
    }

    @Override
    public AppAssessmentResultRespVO getAssessmentResult(Long id) {
        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }
        
        // TODO: 获取用户的测评结果（需要测评结果表）
        // 暂时返回模拟数据
        AppAssessmentResultRespVO respVO = new AppAssessmentResultRespVO();
        respVO.setAssessmentId(id);
        respVO.setTitle(assessment.getTitle());
        respVO.setResultData("测评结果数据");
        respVO.setCompletedTime(LocalDateTime.now());
        respVO.setReport("您的宝宝发展良好，建议继续保持。");
        
        return respVO;
    }

    private String generateAccessToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 校验问卷列表存在
     */
    private void validateQuestionnairesExist(List<AssessmentCreateReqVO.AssessmentQuestionnaireReqVO> questionnaires) {
        if (questionnaires == null || questionnaires.isEmpty()) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        for (AssessmentCreateReqVO.AssessmentQuestionnaireReqVO questionnaire : questionnaires) {
            validateQuestionnaireExists(questionnaire.getQuestionnaireId());
        }
    }

    /**
     * 创建测评问卷关联
     */
    private void createAssessmentQuestionnaires(Long assessmentId, List<AssessmentCreateReqVO.AssessmentQuestionnaireReqVO> questionnaires) {
        for (int i = 0; i < questionnaires.size(); i++) {
            AssessmentCreateReqVO.AssessmentQuestionnaireReqVO questionnaireReq = questionnaires.get(i);

            AssessmentQuestionnaireDO assessmentQuestionnaire = AssessmentQuestionnaireDO.builder()
                    .assessmentId(assessmentId)
                    .questionnaireId(questionnaireReq.getQuestionnaireId())
                    .sortOrder(questionnaireReq.getSortOrder() != null ? questionnaireReq.getSortOrder() : i + 1)
                    .isRequired(questionnaireReq.getIsRequired() != null ? questionnaireReq.getIsRequired() : true)
                    .weight(questionnaireReq.getWeight() != null ? questionnaireReq.getWeight() : java.math.BigDecimal.ONE)
                    .build();

            assessmentQuestionnaireMapper.insert(assessmentQuestionnaire);
        }
    }

    /**
     * 填充测评的问卷信息
     */
    private void fillAssessmentQuestionnaires(AssessmentRespVO assessment) {
        List<AssessmentQuestionnaireDO> assessmentQuestionnaires = assessmentQuestionnaireMapper.selectByAssessmentId(assessment.getId());
        if (assessmentQuestionnaires.isEmpty()) {
            assessment.setQuestionnaires(new ArrayList<>());
            return;
        }

        // 获取问卷信息
        List<Long> questionnaireIds = assessmentQuestionnaires.stream()
                .map(AssessmentQuestionnaireDO::getQuestionnaireId)
                .collect(Collectors.toList());
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        Map<Long, QuestionnaireDO> questionnaireMap = questionnaires.stream()
                .collect(Collectors.toMap(QuestionnaireDO::getId, q -> q));

        // 构建响应对象
        List<AssessmentRespVO.AssessmentQuestionnaireRespVO> questionnaireRespList = assessmentQuestionnaires.stream()
                .map(aq -> {
                    QuestionnaireDO questionnaire = questionnaireMap.get(aq.getQuestionnaireId());
                    AssessmentRespVO.AssessmentQuestionnaireRespVO resp = new AssessmentRespVO.AssessmentQuestionnaireRespVO();
                    resp.setQuestionnaireId(aq.getQuestionnaireId());
                    resp.setSortOrder(aq.getSortOrder());
                    resp.setIsRequired(aq.getIsRequired());
                    resp.setWeight(aq.getWeight());
                    if (questionnaire != null) {
                        resp.setQuestionnaireTitle(questionnaire.getTitle());
                        resp.setQuestionnaireDescription(questionnaire.getDescription());
                        resp.setEstimatedDuration(questionnaire.getEstimatedDuration());
                    }
                    return resp;
                })
                .collect(Collectors.toList());

        assessment.setQuestionnaires(questionnaireRespList);
    }

    /**
     * 填充App端测评的问卷信息
     */
    private void fillAppAssessmentQuestionnaires(AppAssessmentRespVO assessment) {
        List<AssessmentQuestionnaireDO> assessmentQuestionnaires = assessmentQuestionnaireMapper.selectByAssessmentId(assessment.getId());
        if (assessmentQuestionnaires.isEmpty()) {
            assessment.setQuestionnaires(new ArrayList<>());
            return;
        }

        // 获取问卷信息
        List<Long> questionnaireIds = assessmentQuestionnaires.stream()
                .map(AssessmentQuestionnaireDO::getQuestionnaireId)
                .collect(Collectors.toList());
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        Map<Long, QuestionnaireDO> questionnaireMap = questionnaires.stream()
                .collect(Collectors.toMap(QuestionnaireDO::getId, q -> q));

        // 构建响应对象
        List<AppAssessmentRespVO.AppAssessmentQuestionnaireRespVO> questionnaireRespList = assessmentQuestionnaires.stream()
                .map(aq -> {
                    QuestionnaireDO questionnaire = questionnaireMap.get(aq.getQuestionnaireId());
                    AppAssessmentRespVO.AppAssessmentQuestionnaireRespVO resp = new AppAssessmentRespVO.AppAssessmentQuestionnaireRespVO();
                    resp.setQuestionnaireId(aq.getQuestionnaireId());
                    resp.setSortOrder(aq.getSortOrder());
                    resp.setIsRequired(aq.getIsRequired());
                    resp.setIsCompleted(false); // TODO: 根据用户完成情况设置
                    if (questionnaire != null) {
                        resp.setQuestionnaireTitle(questionnaire.getTitle());
                        resp.setQuestionnaireLink(questionnaire.getLink());
                        resp.setEstimatedDuration(questionnaire.getEstimatedDuration());
                    }
                    return resp;
                })
                .collect(Collectors.toList());

        assessment.setQuestionnaires(questionnaireRespList);
    }
}

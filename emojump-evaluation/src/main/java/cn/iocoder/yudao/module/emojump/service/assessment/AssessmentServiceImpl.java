package cn.iocoder.yudao.module.emojump.service.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentParticipateRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentSubmitReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppQuestionnaireSubmitReqVO;
import cn.iocoder.yudao.module.emojump.convert.assessment.AssessmentConvert;
import cn.iocoder.yudao.module.emojump.convert.questionnaire.QuestionnaireConvert;
import cn.iocoder.yudao.module.emojump.convert.questionnaire.QuestionnaireResultConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentQuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentResultDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentQuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentResultMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireResultMapper;
import cn.iocoder.yudao.module.emojump.enums.AssessmentStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireAccessDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireAccessMapper;


import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
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

    @Resource
    private AssessmentResultMapper assessmentResultMapper;

    @Resource
    private QuestionnaireResultMapper questionnaireResultMapper;

    @Resource
    private MemberBabyService memberBabyService;

    @Resource
    private QuestionnaireAccessMapper questionnaireAccessMapper;

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

        // 更新测评问卷关联（智能更新，避免唯一约束冲突）
        updateAssessmentQuestionnaires(updateReqVO.getId(), updateReqVO.getQuestionnaires());
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

    @Override
    public List<QuestionnaireRespVO> getQuestionnairesByAssessmentId(Long assessmentId) {
        // 校验测评存在
        validateAssessmentExists(assessmentId);
        
        // 1. 根据 assessmentId 查询 emo_assessment_questionnaire 表，获取 questionnaire_id 列表
        List<AssessmentQuestionnaireDO> assessmentQuestionnaires = assessmentQuestionnaireMapper.selectByAssessmentId(assessmentId);
        System.out.println("DEBUG: assessmentId=" + assessmentId + ", 查询到的关联记录数量: " + assessmentQuestionnaires.size());
        
        // 如果查询不到数据，可能是租户ID不匹配的问题
        if (assessmentQuestionnaires.isEmpty()) {
            System.out.println("DEBUG: 没有找到测评ID=" + assessmentId + "的关联问卷记录");
            return new ArrayList<>();
        }
        
        // 2. 提取 questionnaire_id 列表
        List<Long> questionnaireIds = CollectionUtils.convertList(assessmentQuestionnaires, AssessmentQuestionnaireDO::getQuestionnaireId);
        System.out.println("DEBUG: 提取到的问卷ID列表: " + questionnaireIds);
        
        // 3. 根据 questionnaire_id 列表查询 emo_questionnaire 表
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        System.out.println("DEBUG: 查询到的问卷数量: " + questionnaires.size());
        
        // 4. 转换为响应对象并返回
        List<QuestionnaireRespVO> result = QuestionnaireConvert.INSTANCE.convertList(questionnaires);
        System.out.println("DEBUG: 最终返回的问卷数量: " + result.size());
        return result;
    }

    // ==================== App端接口实现 ====================

    @Override
    public AppAssessmentRespVO getAppAssessment(Long id, Long babyId) {
        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }

        AppAssessmentRespVO respVO = AssessmentConvert.INSTANCE.convertApp(assessment);

        // 填充问卷信息
        fillAppAssessmentQuestionnaires(respVO);

        // 检查宝宝是否已参与
        if (babyId != null) {
            AssessmentResultDO result = assessmentResultMapper.selectOne(
                    AssessmentResultDO::getAssessmentId, id, AssessmentResultDO::getBabyId, babyId);
            respVO.setIsParticipated(result != null);
            if (result != null) {
                respVO.setParticipateTime(result.getCreateTime());
            }
        } else {
            respVO.setIsParticipated(false);
        }

        return respVO;
    }

    @Override
    public PageResult<AppAssessmentRespVO> getAppAssessmentPage(AppAssessmentPageReqVO pageReqVO) {
        PageResult<AssessmentDO> pageResult = assessmentMapper.selectPage(pageReqVO);
        PageResult<AppAssessmentRespVO> convertPage = AssessmentConvert.INSTANCE.convertAppPage(pageResult);
        fillAssessmentParticipateStatus(convertPage.getList(), getLoginUserId());
        return convertPage;
    }

    @Override
    public PageResult<AppAssessmentRespVO> getPublishedAssessmentPage(AppAssessmentPageReqVO pageReqVO) {
        PageResult<AssessmentDO> pageResult = assessmentMapper.selectPublishedPage(pageReqVO);
        PageResult<AppAssessmentRespVO> convertPage = AssessmentConvert.INSTANCE.convertAppPage(pageResult);
        fillAssessmentParticipateStatus(convertPage.getList(), getLoginUserId());
        return convertPage;
    }

    @Override
    @Transactional
    public AppAssessmentParticipateRespVO participateAssessment(Long assessmentId, Long babyId) {
        AssessmentDO assessment = assessmentMapper.selectById(assessmentId);
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

        // 检查宝宝是否已参与测评
        AssessmentResultDO existingResult = assessmentResultMapper.selectOne(
            AssessmentResultDO::getAssessmentId, assessmentId,
            AssessmentResultDO::getBabyId, babyId
        );
        if (existingResult != null) {
            throw exception(ASSESSMENT_ALREADY_PARTICIPATED);
        }

        // 创建测评结果记录
        AssessmentResultDO assessmentResult = new AssessmentResultDO();
        assessmentResult.setAssessmentId(assessmentId);
        assessmentResult.setBabyId(babyId);
        assessmentResult.setStatus(0); // 进行中
        assessmentResultMapper.insert(assessmentResult);

        // 获取测评关联的问卷信息
        List<AssessmentQuestionnaireDO> assessmentQuestionnaires = assessmentQuestionnaireMapper.selectByAssessmentId(assessmentId);
        if (assessmentQuestionnaires.isEmpty()) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 获取问卷详细信息
        List<Long> questionnaireIds = CollectionUtils.convertList(assessmentQuestionnaires, AssessmentQuestionnaireDO::getQuestionnaireId);
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        Map<Long, QuestionnaireDO> questionnaireMap = CollectionUtils.convertMap(questionnaires, QuestionnaireDO::getId);

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
        respVO.setAssessmentResultId(assessmentResult.getId());
        respVO.setAssessmentId(assessmentId);
        respVO.setAssessmentTitle(assessment.getTitle());
        respVO.setQuestionnaires(participateQuestionnaires);
        respVO.setAccessToken(generateAccessToken());

        return respVO;
    }

    @Override
    @Transactional
    public void submitQuestionnaireResult(AppQuestionnaireSubmitReqVO submitReqVO) {
        // 1. 校验测评结果存在
        AssessmentResultDO result = assessmentResultMapper.selectById(submitReqVO.getAssessmentResultId());
        if (result == null) {
            throw exception(ASSESSMENT_NOT_EXISTS); // 或者更具体的错误码，例如 ASSESSMENT_RESULT_NOT_FOUND
        }

        // 2. 校验问卷属于该测评
        boolean questionnaireExistsInAssessment = assessmentQuestionnaireMapper.existsByAssessmentIdAndQuestionnaireId(
                result.getAssessmentId(), submitReqVO.getQuestionnaireId());
        if (!questionnaireExistsInAssessment) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS); // 或者 QUESTIONNAIRE_NOT_IN_ASSESSMENT
        }

        // 3. 保存单个问卷的结果
        QuestionnaireResultDO questionnaireResult = QuestionnaireResultConvert.INSTANCE.convert(submitReqVO);
        questionnaireResultMapper.insert(questionnaireResult);

        // 4. 更新问卷的完成次数
        questionnaireMapper.updateById(new QuestionnaireDO().setId(submitReqVO.getQuestionnaireId())
                .setCompletionCount(questionnaireMapper.selectById(submitReqVO.getQuestionnaireId()).getCompletionCount() + 1));
    }


    @Override
    @Transactional
    public void submitAssessment(AppAssessmentSubmitReqVO submitReqVO) {
        AssessmentDO assessment = assessmentMapper.selectById(submitReqVO.getAssessmentId());
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }

        Long babyId = submitReqVO.getBabyId();
        // 1. 获取关联问卷ID列表
        List<AssessmentQuestionnaireDO> assessmentQuestionnaires = assessmentQuestionnaireMapper.selectByAssessmentId(submitReqVO.getAssessmentId());
        List<Long> questionnaireIds = CollectionUtils.convertList(assessmentQuestionnaires, AssessmentQuestionnaireDO::getQuestionnaireId);
        // 2. 检查每个问卷是否有访问记录
        for (Long questionnaireId : questionnaireIds) {
            long count = questionnaireAccessMapper.countByQuestionnaireIdAndBabyId(questionnaireId, babyId);
            if (count == 0) {
                throw exception(QUESTIONNAIRE_NOT_COMPLETED); // 需定义此异常码
            }
        }

        // 3. 查询测评结果
        AssessmentResultDO result = assessmentResultMapper.selectOne(
                AssessmentResultDO::getAssessmentId, submitReqVO.getAssessmentId(),
                AssessmentResultDO::getBabyId, babyId);
        if (result == null) {
            throw exception(ASSESSMENT_NOT_EXISTS); // 宝宝未参与该测评
        }

        // 4. 更新测评结果为已完成
        result.setStatus(1); // 已完成
        result.setCompletedTime(LocalDateTime.now());
        // TODO: 根据所有问卷结果计算总分、总评级和总报告
        // result.setOverallScore(...)
        // result.setOverallLevel(...)
        // result.setOverallReport(...)
        assessmentResultMapper.updateById(result);

        // 5. 更新测评的当前参与人数
        assessmentMapper.updateById(new AssessmentDO().setId(submitReqVO.getAssessmentId())
                .setCurrentParticipants(assessment.getCurrentParticipants() + 1));
    }

    @Override
    public PageResult<AppAssessmentRespVO> getBabyAssessmentPage(AppAssessmentPageReqVO pageReqVO) {
        // 1. 查询宝宝参与的测评结果
        List<AssessmentResultDO> myResults = assessmentResultMapper.selectList(
                AssessmentResultDO::getBabyId, pageReqVO.getBabyId());
        if (myResults == null || myResults.isEmpty()) {
            return PageResult.empty();
        }
        List<Long> assessmentIds = CollectionUtils.convertList(myResults, AssessmentResultDO::getAssessmentId);

        // 2. 分页查询测评信息
        PageResult<AssessmentDO> pageResult = assessmentMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<AssessmentDO>()
                .in(AssessmentDO::getId, assessmentIds)
                .eqIfPresent(AssessmentDO::getType, pageReqVO.getType())
                .eqIfPresent(AssessmentDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(AssessmentDO::getId));

        PageResult<AppAssessmentRespVO> convertPage = AssessmentConvert.INSTANCE.convertAppPage(pageResult);

        // 3. 填充问卷信息和参与状态
        Map<Long, AssessmentResultDO> resultMap = CollectionUtils.convertMap(myResults, AssessmentResultDO::getAssessmentId);
        for (AppAssessmentRespVO assessmentVO : convertPage.getList()) {
            fillAppAssessmentQuestionnaires(assessmentVO);
            assessmentVO.setIsParticipated(true);
            AssessmentResultDO result = resultMap.get(assessmentVO.getId());
            if (result != null) {
                assessmentVO.setParticipateTime(result.getCreateTime());
            }
        }

        return convertPage;
    }

    @Override
    public AppAssessmentResultRespVO getAssessmentResult(Long id, Long babyId) {
        AssessmentResultDO result = assessmentResultMapper.selectOne(
                AssessmentResultDO::getAssessmentId, id,
                AssessmentResultDO::getBabyId, babyId);

        if (result == null) {
            throw exception(ASSESSMENT_NOT_EXISTS); // 或者 RESULT_NOT_FOUND
        }

        AssessmentDO assessment = assessmentMapper.selectById(id);
        if (assessment == null) {
            throw exception(ASSESSMENT_NOT_EXISTS);
        }

        AppAssessmentResultRespVO respVO = new AppAssessmentResultRespVO();
        respVO.setAssessmentId(id);
        respVO.setTitle(assessment.getTitle());
        respVO.setResultData(result.getOverallReport()); // 暂时用报告字段
        respVO.setCompletedTime(result.getCompletedTime());
        respVO.setReport(result.getOverallReport());

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
        List<Long> questionnaireIds = CollectionUtils.convertList(assessmentQuestionnaires, AssessmentQuestionnaireDO::getQuestionnaireId);
        if (org.springframework.util.CollectionUtils.isEmpty(questionnaireIds)) {
            assessment.setQuestionnaires(new ArrayList<>());
            return;
        }
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        Map<Long, QuestionnaireDO> questionnaireMap = CollectionUtils.convertMap(questionnaires, QuestionnaireDO::getId);

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
        List<Long> questionnaireIds = CollectionUtils.convertList(assessmentQuestionnaires, AssessmentQuestionnaireDO::getQuestionnaireId);
        if (org.springframework.util.CollectionUtils.isEmpty(questionnaireIds)) {
            assessment.setQuestionnaires(new ArrayList<>());
            return;
        }
        List<QuestionnaireDO> questionnaires = questionnaireMapper.selectBatchIds(questionnaireIds);
        Map<Long, QuestionnaireDO> questionnaireMap = CollectionUtils.convertMap(questionnaires, QuestionnaireDO::getId);

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

    /**
     * 智能更新测评问卷关联关系
     * 通过比较新旧关联关系，只删除需要删除的，只添加需要添加的，避免唯一约束冲突
     *
     * @param assessmentId 测评ID
     * @param newQuestionnaires 新的问卷关联列表
     */
    private void updateAssessmentQuestionnaires(Long assessmentId, List<AssessmentCreateReqVO.AssessmentQuestionnaireReqVO> newQuestionnaires) {
        // 1. 获取现有的关联关系
        List<AssessmentQuestionnaireDO> existingRelations = assessmentQuestionnaireMapper.selectByAssessmentId(assessmentId);

        // 2. 构建现有关联的Map，key为questionnaireId
        Map<Long, AssessmentQuestionnaireDO> existingMap = existingRelations.stream()
                .collect(Collectors.toMap(AssessmentQuestionnaireDO::getQuestionnaireId, Function.identity()));

        // 3. 构建新关联的Set
        Set<Long> newQuestionnaireIds = newQuestionnaires.stream()
                .map(AssessmentCreateReqVO.AssessmentQuestionnaireReqVO::getQuestionnaireId)
                .collect(Collectors.toSet());

        // 4. 找出需要删除的关联（存在于旧关联中，但不存在于新关联中）
        List<Long> toDeleteIds = existingRelations.stream()
                .filter(relation -> !newQuestionnaireIds.contains(relation.getQuestionnaireId()))
                .map(AssessmentQuestionnaireDO::getId)
                .collect(Collectors.toList());

        // 5. 找出需要添加的关联（存在于新关联中，但不存在于旧关联中）
        List<AssessmentCreateReqVO.AssessmentQuestionnaireReqVO> toAdd = newQuestionnaires.stream()
                .filter(newRelation -> !existingMap.containsKey(newRelation.getQuestionnaireId()))
                .collect(Collectors.toList());

        // 6. 找出需要更新的关联（存在于新旧关联中，但属性可能不同）
        List<AssessmentQuestionnaireDO> toUpdate = newQuestionnaires.stream()
                .filter(newRelation -> existingMap.containsKey(newRelation.getQuestionnaireId()))
                .map(newRelation -> {
                    AssessmentQuestionnaireDO existing = existingMap.get(newRelation.getQuestionnaireId());
                    // 检查是否需要更新
                    if (!Objects.equals(existing.getSortOrder(), newRelation.getSortOrder()) ||
                        !Objects.equals(existing.getIsRequired(), newRelation.getIsRequired()) ||
                        !Objects.equals(existing.getWeight(), newRelation.getWeight())) {
                        // 需要更新
                        existing.setSortOrder(newRelation.getSortOrder());
                        existing.setIsRequired(newRelation.getIsRequired());
                        existing.setWeight(newRelation.getWeight());
                        return existing;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 7. 执行删除操作
        if (!toDeleteIds.isEmpty()) {
            assessmentQuestionnaireMapper.deleteBatchIds(toDeleteIds);
        }

        // 8. 执行添加操作
        if (!toAdd.isEmpty()) {
            createAssessmentQuestionnaires(assessmentId, toAdd);
        }

        // 9. 执行更新操作
        if (!toUpdate.isEmpty()) {
            toUpdate.forEach(assessmentQuestionnaireMapper::updateById);
        }
    }

    /**
     * 填充测评的问卷信息和参与状态（支持多宝宝）
     */
    private void fillAssessmentParticipateStatus(List<AppAssessmentRespVO> assessmentList, Long userId) {
        if (userId != null) {
            // 获取当前用户所有宝宝
            List<MemberBabyDO> babyList = memberBabyService.getBabyListByUserId(userId);
            List<Long> babyIds = babyList.stream().map(MemberBabyDO::getId).collect(Collectors.toList());
            List<Long> assessmentIds = CollectionUtils.convertList(assessmentList, AppAssessmentRespVO::getId);
            if (!assessmentIds.isEmpty() && !babyIds.isEmpty()) {
                // 查询所有宝宝在所有测评下的参与记录
                List<AssessmentResultDO> results = assessmentResultMapper.selectList(new LambdaQueryWrapperX<AssessmentResultDO>()
                        .in(AssessmentResultDO::getAssessmentId, assessmentIds)
                        .in(AssessmentResultDO::getBabyId, babyIds));
                // Map<测评ID, 是否有宝宝参与过>
                Map<Long, Boolean> assessmentParticipatedMap = new HashMap<>();
                for (AssessmentResultDO result : results) {
                    assessmentParticipatedMap.put(result.getAssessmentId(), true);
                }
                // Map<测评ID, 参与时间>（取第一个参与的时间）
                Map<Long, LocalDateTime> assessmentParticipateTimeMap = new HashMap<>();
                for (AssessmentResultDO result : results) {
                    assessmentParticipateTimeMap.putIfAbsent(result.getAssessmentId(), result.getCreateTime());
                }
                for (AppAssessmentRespVO assessmentVO : assessmentList) {
                    fillAppAssessmentQuestionnaires(assessmentVO);
                    boolean participated = assessmentParticipatedMap.getOrDefault(assessmentVO.getId(), false);
                    assessmentVO.setIsParticipated(participated);
                    if (participated) {
                        assessmentVO.setParticipateTime(assessmentParticipateTimeMap.get(assessmentVO.getId()));
                    }
                }
            } else {
                assessmentList.forEach(assessmentVO -> {
                    fillAppAssessmentQuestionnaires(assessmentVO);
                    assessmentVO.setIsParticipated(false);
                });
            }
        } else {
            assessmentList.forEach(assessmentVO -> {
                fillAppAssessmentQuestionnaires(assessmentVO);
                assessmentVO.setIsParticipated(false);
            });
        }
    }
}
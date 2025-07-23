package cn.iocoder.yudao.module.emojump.service.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
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

import javax.validation.Valid;
import java.util.List;

/**
 * 测评管理 Service 接口
 */
public interface AssessmentService {

    /**
     * 创建测评
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAssessment(@Valid AssessmentCreateReqVO createReqVO);

    /**
     * 更新测评
     *
     * @param updateReqVO 更新信息
     */
    void updateAssessment(@Valid AssessmentUpdateReqVO updateReqVO);

    /**
     * 删除测评
     *
     * @param id 编号
     */
    void deleteAssessment(Long id);

    /**
     * 获得测评
     *
     * @param id 编号
     * @return 测评
     */
    AssessmentRespVO getAssessment(Long id);

    /**
     * 获得测评分页
     *
     * @param pageReqVO 分页查询
     * @return 测评分页
     */
    PageResult<AssessmentRespVO> getAssessmentPage(AssessmentPageReqVO pageReqVO);

    /**
     * 发布测评
     *
     * @param id 编号
     */
    void publishAssessment(Long id);

    /**
     * 取消发布测评
     *
     * @param id 编号
     */
    void unpublishAssessment(Long id);

    /**
     * 获取可选择的问卷列表
     *
     * @param pageReqVO 分页查询
     * @return 问卷分页
     */
    PageResult<QuestionnaireRespVO> getAvailableQuestionnaires(QuestionnairePageReqVO pageReqVO);

    /**
     * 根据测评ID获取关联的问卷列表
     *
     * @param assessmentId 测评编号
     * @return 问卷列表
     */
    List<QuestionnaireRespVO> getQuestionnairesByAssessmentId(Long assessmentId);

    // ==================== App端接口 ====================

    /**
     * 获得测评信息（App端）
     *
     * @param id 编号
     * @param babyId 宝宝编号
     * @return 测评信息
     */
    AppAssessmentRespVO getAppAssessment(Long id, Long babyId);

    /**
     * 获得测评分页（App端）
     *
     * @param pageReqVO 分页查询
     * @return 测评分页
     */
    PageResult<AppAssessmentRespVO> getAppAssessmentPage(AppAssessmentPageReqVO pageReqVO);

    /**
     * 获得已发布测评分页（App端）
     *
     * @param pageReqVO 分页查询
     * @return 测评分页
     */
    PageResult<AppAssessmentRespVO> getPublishedAssessmentPage(AppAssessmentPageReqVO pageReqVO);

    /**
     * 参与测评
     *
     * @param assessmentId 测评编号
     * @param babyId 宝宝编号
     * @return 参与结果
     */
    AppAssessmentParticipateRespVO participateAssessment(Long assessmentId, Long babyId);

    /**
     * 提交单个问卷结果
     *
     * @param submitReqVO 提交信息
     */
    void submitQuestionnaireResult(AppQuestionnaireSubmitReqVO submitReqVO);

    /**
     * 提交测评结果
     *
     * @param submitReqVO 提交信息
     */
    void submitAssessment(AppAssessmentSubmitReqVO submitReqVO);

    /**
     * 获得我的测评分页
     *
     * @param pageReqVO 分页查询
     * @return 测评分页
     */
    PageResult<AppAssessmentRespVO> getBabyAssessmentPage(AppAssessmentPageReqVO pageReqVO);

    /**
     * 获得测评结果
     *
     * @param id 测评编号
     * @param babyId 宝宝编号
     * @return 测评结果
     */
    AppAssessmentResultRespVO getAssessmentResult(Long id, Long babyId);
} 

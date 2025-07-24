package cn.iocoder.yudao.module.emojump.service.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireAnswerSubmitReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultUpdateReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppBabyQuestionnaireResultRespVO;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 问卷结果 Service 接口
 *
 * @author 芋道源码
 */
public interface QuestionnaireResultService {

    /**
     * 创建问卷结果
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createQuestionnaireResult(@Valid QuestionnaireResultCreateReqVO createReqVO);

    /**
     * 更新问卷结果
     *
     * @param updateReqVO 更新信息
     */
    void updateQuestionnaireResult(@Valid QuestionnaireResultUpdateReqVO updateReqVO);

    /**
     * 删除问卷结果
     *
     * @param id 编号
     */
    void deleteQuestionnaireResult(Long id);

    /**
     * 获得问卷结果
     *
     * @param id 编号
     * @return 问卷结果
     */
    EmoQuestionnaireResultDO getQuestionnaireResult(Long id);

    /**
     * 获得问卷结果列表
     *
     * @param ids 编号
     * @return 问卷结果列表
     */
    List<EmoQuestionnaireResultDO> getQuestionnaireResultList(Collection<Long> ids);

    /**
     * 获得问卷结果分页
     *
     * @param pageReqVO 分页查询
     * @return 问卷结果分页
     */
    PageResult<EmoQuestionnaireResultDO> getQuestionnaireResultPage(QuestionnaireResultPageReqVO pageReqVO);

    /**
     * 获得问卷结果列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 问卷结果列表
     */
    List<EmoQuestionnaireResultDO> getQuestionnaireResultList(QuestionnaireResultPageReqVO exportReqVO);

    /**
     * 根据测评结果ID获取问卷结果列表
     *
     * @param assessmentResultId 测评结果ID
     * @return 问卷结果列表
     */
    List<EmoQuestionnaireResultDO> getQuestionnaireResultListByAssessmentResultId(Long assessmentResultId);

    /**
     * 根据问卷ID获取问卷结果列表
     *
     * @param questionnaireId 问卷ID
     * @return 问卷结果列表
     */
    List<EmoQuestionnaireResultDO> getQuestionnaireResultListByQuestionnaireId(Long questionnaireId);

    /**
     * 根据问卷ID和时间范围统计问卷结果数量
     *
     * @param questionnaireId 问卷ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 问卷结果数量
     */
    Long getQuestionnaireResultCountByQuestionnaireIdAndTime(Long questionnaireId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 批量删除问卷结果
     *
     * @param ids 编号列表
     */
    void deleteQuestionnaireResultBatch(Collection<Long> ids);

    /**
     * 提交问卷答案
     *
     * @param submitReqVO 提交信息
     * @return 问卷结果ID
     */
    Long submitQuestionnaireAnswer(@Valid QuestionnaireAnswerSubmitReqVO submitReqVO);

    /**
     * 获取宝宝的问卷测评结果
     *
     * @param babyId 宝宝ID
     * @return 宝宝的问卷测评结果列表
     */
    List<AppBabyQuestionnaireResultRespVO> getBabyQuestionnaireResults(Long babyId);

}

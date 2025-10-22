package cn.iocoder.yudao.module.emojump.service.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.UserAssessmentRecordsRespVO;

import javax.validation.Valid;
import java.util.List;

/**
 * 测评结果 Service 接口
 *
 * @author 芋道源码
 */
public interface AssessmentResultService {

    /**
     * 获得测评结果分页
     *
     * @param pageReqVO 分页查询
     * @return 测评结果分页
     */
    PageResult<AssessmentResultRespVO> getAssessmentResultPage(@Valid AssessmentResultPageReqVO pageReqVO);

    /**
     * 获得测评结果
     *
     * @param id 编号
     * @return 测评结果
     */
    AssessmentResultRespVO getAssessmentResult(Long id);

    /**
     * 生成测评结果
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @return 测评结果ID
     */
    Long generateAssessmentResult(Long assessmentId, Long babyId);

    /**
     * 获取历史测评结果列表
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @return 历史测评结果列表
     */
    List<AssessmentResultRespVO> getHistoryAssessmentResults(Long assessmentId, Long babyId);

    /**
     * 获取最新测评结果记录
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @return 最新测评结果记录
     */
    AssessmentResultRespVO getLatestAssessmentResult(Long assessmentId, Long babyId);

    /**
     * 获取用户测评记录
     *
     * @param userId 用户ID
     * @return 用户测评记录
     */
    UserAssessmentRecordsRespVO getUserAssessmentRecords(Long userId);

    /**
     * 检查指定测评和宝宝的所有测评结果是否都已完成
     *
     * @param assessmentId 测评ID
     * @param babyId 宝宝ID
     * @return 如果所有测评结果状态都是1(已完成)返回true，否则返回false
     */
    Boolean checkAllAssessmentResultsCompleted(Long assessmentId, Long babyId);

    /**
     * 获取宝宝的所有测评结果
     *
     * @param babyId 宝宝ID
     * @return 所有测评结果列表
     */
    List<AssessmentResultRespVO> getAllAssessmentResultsByBabyId(Long babyId);

}

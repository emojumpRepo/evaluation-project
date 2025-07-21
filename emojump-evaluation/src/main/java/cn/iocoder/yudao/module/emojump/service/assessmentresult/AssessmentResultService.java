package cn.iocoder.yudao.module.emojump.service.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;

import javax.validation.Valid;

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

}

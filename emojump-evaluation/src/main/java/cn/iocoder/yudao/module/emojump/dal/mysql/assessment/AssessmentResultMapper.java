package cn.iocoder.yudao.module.emojump.dal.mysql.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentResultDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评结果 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AssessmentResultMapper extends BaseMapperX<AssessmentResultDO> {

    default PageResult<AssessmentResultDO> selectPage(AssessmentResultPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssessmentResultDO>()
                .eqIfPresent(AssessmentResultDO::getAssessmentId, reqVO.getAssessmentId())
                .eqIfPresent(AssessmentResultDO::getBabyId, reqVO.getBabyId())
                .eqIfPresent(AssessmentResultDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(AssessmentResultDO::getCompletedTime, reqVO.getCompletedTime())
                .orderByDesc(AssessmentResultDO::getId));
    }

}

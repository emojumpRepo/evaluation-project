package cn.iocoder.yudao.module.emojump.dal.mysql.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AssessmentMapper extends BaseMapperX<AssessmentDO> {

    default PageResult<AssessmentDO> selectPage(AssessmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssessmentDO>()
                .likeIfPresent(AssessmentDO::getTitle, reqVO.getTitle())
                .eqIfPresent(AssessmentDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AssessmentDO::getType, reqVO.getType())
                .betweenIfPresent(AssessmentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AssessmentDO::getId));
    }

    default PageResult<AssessmentDO> selectPage(AppAssessmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssessmentDO>()
                .eqIfPresent(AssessmentDO::getType, reqVO.getType())
                .eqIfPresent(AssessmentDO::getStatus, reqVO.getStatus())
                .orderByDesc(AssessmentDO::getId));
    }

    default PageResult<AssessmentDO> selectPublishedPage(AppAssessmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssessmentDO>()
                .eq(AssessmentDO::getStatus, 1) // 已发布状态
                .eqIfPresent(AssessmentDO::getType, reqVO.getType())
                .orderByDesc(AssessmentDO::getId));
    }

} 

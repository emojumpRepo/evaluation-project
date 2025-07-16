package cn.iocoder.yudao.module.emojump.convert.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.vo.assessment.AssessmentCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.vo.assessment.AssessmentRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.vo.assessment.AssessmentUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.vo.assessment.AppAssessmentRespVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 测评 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface AssessmentConvert {

    AssessmentConvert INSTANCE = Mappers.getMapper(AssessmentConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    AssessmentDO convert(AssessmentCreateReqVO bean);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentParticipants", ignore = true)
    AssessmentDO convert(AssessmentUpdateReqVO bean);

    @Mapping(target = "questionnaires", ignore = true)
    AssessmentRespVO convert(AssessmentDO bean);

    List<AssessmentRespVO> convertList(List<AssessmentDO> list);

    PageResult<AssessmentRespVO> convertPage(PageResult<AssessmentDO> page);

    // App端转换
    @Mapping(target = "questionnaires", ignore = true)
    @Mapping(target = "isParticipated", ignore = true)
    @Mapping(target = "participateTime", ignore = true)
    AppAssessmentRespVO convertApp(AssessmentDO bean);

    List<AppAssessmentRespVO> convertAppList(List<AssessmentDO> list);

    PageResult<AppAssessmentRespVO> convertAppPage(PageResult<AssessmentDO> page);

} 

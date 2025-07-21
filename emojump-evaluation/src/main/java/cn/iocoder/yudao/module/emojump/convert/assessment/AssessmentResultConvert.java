package cn.iocoder.yudao.module.emojump.convert.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentResultDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 测评结果 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface AssessmentResultConvert {

    AssessmentResultConvert INSTANCE = Mappers.getMapper(AssessmentResultConvert.class);

    AssessmentResultRespVO convert(AssessmentResultDO bean);

    List<AssessmentResultRespVO> convertList(List<AssessmentResultDO> list);

    PageResult<AssessmentResultRespVO> convertPage(PageResult<AssessmentResultDO> page);

}

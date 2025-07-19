package cn.iocoder.yudao.module.emojump.convert.questionnaire;

import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppQuestionnaireSubmitReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireResultDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 问卷结果 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface QuestionnaireResultConvert {

    QuestionnaireResultConvert INSTANCE = Mappers.getMapper(QuestionnaireResultConvert.class);

    @Mapping(target = "id", ignore = true)
    QuestionnaireResultDO convert(AppQuestionnaireSubmitReqVO bean);
}

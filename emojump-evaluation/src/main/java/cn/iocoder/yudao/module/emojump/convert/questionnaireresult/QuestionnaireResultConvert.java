package cn.iocoder.yudao.module.emojump.convert.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultExcelVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultListRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 问卷结果 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface QuestionnaireResultConvert {

    QuestionnaireResultConvert INSTANCE = Mappers.getMapper(QuestionnaireResultConvert.class);

    EmoQuestionnaireResultDO convert(QuestionnaireResultCreateReqVO bean);

    EmoQuestionnaireResultDO convert(QuestionnaireResultUpdateReqVO bean);

    QuestionnaireResultRespVO convert(EmoQuestionnaireResultDO bean);

    List<QuestionnaireResultRespVO> convertList(List<EmoQuestionnaireResultDO> list);

    PageResult<QuestionnaireResultRespVO> convertPage(PageResult<EmoQuestionnaireResultDO> page);

    List<QuestionnaireResultExcelVO> convertList02(List<EmoQuestionnaireResultDO> list);

    AppQuestionnaireResultListRespVO convertToListVO(EmoQuestionnaireResultDO bean);
    List<AppQuestionnaireResultListRespVO> convertToListVOList(List<EmoQuestionnaireResultDO> list);

    AppQuestionnaireResultVO convertToAppVO(EmoQuestionnaireResultDO bean);

}

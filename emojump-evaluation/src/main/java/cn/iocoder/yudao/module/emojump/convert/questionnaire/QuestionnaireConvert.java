package cn.iocoder.yudao.module.emojump.convert.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.*;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireAccessRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 问卷 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface QuestionnaireConvert {

    QuestionnaireConvert INSTANCE = Mappers.getMapper(QuestionnaireConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "accessCount", ignore = true)
    @Mapping(target = "completionCount", ignore = true)
    QuestionnaireDO convert(QuestionnaireCreateReqVO bean);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "accessCount", ignore = true)
    @Mapping(target = "completionCount", ignore = true)
    QuestionnaireDO convert(QuestionnaireUpdateReqVO bean);

    QuestionnaireRespVO convert(QuestionnaireDO bean);

    List<QuestionnaireRespVO> convertList(List<QuestionnaireDO> list);

    PageResult<QuestionnaireRespVO> convertPage(PageResult<QuestionnaireDO> page);

    // App端转换
    @Mapping(target = "isPopular", ignore = true)
    AppQuestionnaireRespVO convertApp(QuestionnaireDO bean);

    List<AppQuestionnaireRespVO> convertAppList(List<QuestionnaireDO> list);

    PageResult<AppQuestionnaireRespVO> convertAppPage(PageResult<QuestionnaireDO> page);

} 

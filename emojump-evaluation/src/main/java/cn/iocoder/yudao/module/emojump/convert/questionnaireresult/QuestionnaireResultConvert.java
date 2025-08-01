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
import java.util.Map;

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

    /**
     * 转换分页结果并填充关联数据
     */
    default PageResult<QuestionnaireResultRespVO> convertPageWithAssociations(PageResult<EmoQuestionnaireResultDO> page, 
                                                                               Map<Long, String> assessmentTitleMap,
                                                                               Map<Long, String> babyNameMap,
                                                                               Map<Long, String> questionnaireTitleMap) {
        if (page == null) {
            return null;
        }
        
        List<QuestionnaireResultRespVO> list = convertListWithAssociations(page.getList(), 
                assessmentTitleMap, babyNameMap, questionnaireTitleMap);
        return new PageResult<>(list, page.getTotal());
    }

    /**
     * 转换列表并填充关联数据
     */
    default List<QuestionnaireResultRespVO> convertListWithAssociations(List<EmoQuestionnaireResultDO> list,
                                                                         Map<Long, String> assessmentTitleMap,
                                                                         Map<Long, String> babyNameMap,
                                                                         Map<Long, String> questionnaireTitleMap) {
        if (list == null) {
            return null;
        }

        List<QuestionnaireResultRespVO> result = convertList(list);
        
        // 填充关联数据
        for (int i = 0; i < result.size() && i < list.size(); i++) {
            QuestionnaireResultRespVO respVO = result.get(i);
            EmoQuestionnaireResultDO sourceVO = list.get(i);
            
            if (sourceVO.getAssessmentId() != null) {
                respVO.setAssessmentTitle(assessmentTitleMap.get(sourceVO.getAssessmentId()));
            }
            if (sourceVO.getBabyId() != null) {
                respVO.setBabyName(babyNameMap.get(sourceVO.getBabyId()));
            }
            if (sourceVO.getQuestionnaireId() != null) {
                respVO.setQuestionnaireTitle(questionnaireTitleMap.get(sourceVO.getQuestionnaireId()));
            }
        }
        
        return result;
    }

}

package cn.iocoder.yudao.module.emojump.dal.mysql.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultPageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 问卷结果 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface EmoQuestionnaireResultMapper extends BaseMapperX<EmoQuestionnaireResultDO> {

    default PageResult<EmoQuestionnaireResultDO> selectPage(QuestionnaireResultPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                .eqIfPresent(EmoQuestionnaireResultDO::getAssessmentResultId, reqVO.getAssessmentResultId())
                .eqIfPresent(EmoQuestionnaireResultDO::getAssessmentId, reqVO.getAssessmentId())
                .eqIfPresent(EmoQuestionnaireResultDO::getBabyId, reqVO.getBabyId())
                .eqIfPresent(EmoQuestionnaireResultDO::getQuestionnaireId, reqVO.getQuestionnaireId())
                .likeIfPresent(EmoQuestionnaireResultDO::getLevel, reqVO.getLevel())
                .betweenIfPresent(EmoQuestionnaireResultDO::getCompletedTime, reqVO.getCompletedTime())
                .betweenIfPresent(EmoQuestionnaireResultDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmoQuestionnaireResultDO::getId));
    }

    default List<EmoQuestionnaireResultDO> selectList(QuestionnaireResultPageReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                .eqIfPresent(EmoQuestionnaireResultDO::getAssessmentResultId, reqVO.getAssessmentResultId())
                .eqIfPresent(EmoQuestionnaireResultDO::getAssessmentId, reqVO.getAssessmentId())
                .eqIfPresent(EmoQuestionnaireResultDO::getBabyId, reqVO.getBabyId())
                .eqIfPresent(EmoQuestionnaireResultDO::getQuestionnaireId, reqVO.getQuestionnaireId())
                .likeIfPresent(EmoQuestionnaireResultDO::getLevel, reqVO.getLevel())
                .betweenIfPresent(EmoQuestionnaireResultDO::getCompletedTime, reqVO.getCompletedTime())
                .betweenIfPresent(EmoQuestionnaireResultDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(EmoQuestionnaireResultDO::getId));
    }

    /**
     * 根据测评结果ID查询问卷结果列表
     */
    default List<EmoQuestionnaireResultDO> selectListByAssessmentResultId(Long assessmentResultId) {
        return selectList(EmoQuestionnaireResultDO::getAssessmentResultId, assessmentResultId);
    }

    /**
     * 根据问卷ID查询问卷结果列表
     */
    default List<EmoQuestionnaireResultDO> selectListByQuestionnaireId(Long questionnaireId) {
        return selectList(EmoQuestionnaireResultDO::getQuestionnaireId, questionnaireId);
    }

    /**
     * 根据问卷ID和时间范围查询问卷结果数量
     */
    default Long selectCountByQuestionnaireIdAndTime(Long questionnaireId, LocalDateTime startTime, LocalDateTime endTime) {
        return selectCount(new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                .eq(EmoQuestionnaireResultDO::getQuestionnaireId, questionnaireId)
                .betweenIfPresent(EmoQuestionnaireResultDO::getCompletedTime, startTime, endTime));
    }

}

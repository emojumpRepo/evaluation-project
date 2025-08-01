package cn.iocoder.yudao.module.emojump.dal.mysql.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultPageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 根据查询条件分页查询问卷结果（支持关联查询）
     */
    @Select({
        "<script>",
        "SELECT DISTINCT qr.* FROM emo_questionnaire_result qr",
        "<if test='reqVO.assessmentId != null or reqVO.assessmentTitle != null'>",
        " LEFT JOIN emo_assessment a ON qr.assessment_id = a.id",
        "</if>",
        "<if test='reqVO.babyName != null'>", 
        " LEFT JOIN member_baby mb ON qr.baby_id = mb.id",
        "</if>",
        "<if test='reqVO.questionnaireTitle != null'>",
        " LEFT JOIN emo_questionnaire q ON qr.questionnaire_id = q.id",
        "</if>",
        " WHERE qr.deleted = 0",
        "<if test='reqVO.assessmentResultId != null'>",
        " AND qr.assessment_result_id = #{reqVO.assessmentResultId}",
        "</if>",
        "<if test='reqVO.assessmentId != null'>",
        " AND qr.assessment_id = #{reqVO.assessmentId}",
        "</if>",
        "<if test='reqVO.babyId != null'>",
        " AND qr.baby_id = #{reqVO.babyId}",
        "</if>",
        "<if test='reqVO.questionnaireId != null'>",
        " AND qr.questionnaire_id = #{reqVO.questionnaireId}",
        "</if>",
        "<if test='reqVO.level != null and reqVO.level != \"\"'>",
        " AND qr.level LIKE CONCAT('%', #{reqVO.level}, '%')",
        "</if>",
        "<if test='reqVO.assessmentTitle != null and reqVO.assessmentTitle != \"\"'>",
        " AND a.title LIKE CONCAT('%', #{reqVO.assessmentTitle}, '%')",
        "</if>",
        "<if test='reqVO.babyName != null and reqVO.babyName != \"\"'>",
        " AND mb.name LIKE CONCAT('%', #{reqVO.babyName}, '%')",
        "</if>",
        "<if test='reqVO.questionnaireTitle != null and reqVO.questionnaireTitle != \"\"'>",
        " AND q.title LIKE CONCAT('%', #{reqVO.questionnaireTitle}, '%')",
        "</if>",
        "<if test='reqVO.completedTime != null and reqVO.completedTime.length == 2'>",
        " AND qr.completed_time BETWEEN #{reqVO.completedTime[0]} AND #{reqVO.completedTime[1]}",
        "</if>",
        "<if test='reqVO.createTime != null and reqVO.createTime.length == 2'>",
        " AND qr.create_time BETWEEN #{reqVO.createTime[0]} AND #{reqVO.createTime[1]}",
        "</if>",
        " ORDER BY qr.id DESC",
        "</script>"
    })
    List<EmoQuestionnaireResultDO> selectPageWithJoin(@Param("reqVO") QuestionnaireResultPageReqVO reqVO);

    /**
     * 统计关联查询的总数
     */
    @Select({
        "<script>",
        "SELECT COUNT(DISTINCT qr.id) FROM emo_questionnaire_result qr",
        "<if test='reqVO.assessmentId != null or reqVO.assessmentTitle != null'>",
        " LEFT JOIN emo_assessment a ON qr.assessment_id = a.id",
        "</if>",
        "<if test='reqVO.babyName != null'>", 
        " LEFT JOIN member_baby mb ON qr.baby_id = mb.id",
        "</if>",
        "<if test='reqVO.questionnaireTitle != null'>",
        " LEFT JOIN emo_questionnaire q ON qr.questionnaire_id = q.id",
        "</if>",
        " WHERE qr.deleted = 0",
        "<if test='reqVO.assessmentResultId != null'>",
        " AND qr.assessment_result_id = #{reqVO.assessmentResultId}",
        "</if>",
        "<if test='reqVO.assessmentId != null'>",
        " AND qr.assessment_id = #{reqVO.assessmentId}",
        "</if>",
        "<if test='reqVO.babyId != null'>",
        " AND qr.baby_id = #{reqVO.babyId}",
        "</if>",
        "<if test='reqVO.questionnaireId != null'>",
        " AND qr.questionnaire_id = #{reqVO.questionnaireId}",
        "</if>",
        "<if test='reqVO.level != null and reqVO.level != \"\"'>",
        " AND qr.level LIKE CONCAT('%', #{reqVO.level}, '%')",
        "</if>",
        "<if test='reqVO.assessmentTitle != null and reqVO.assessmentTitle != \"\"'>",
        " AND a.title LIKE CONCAT('%', #{reqVO.assessmentTitle}, '%')",
        "</if>",
        "<if test='reqVO.babyName != null and reqVO.babyName != \"\"'>",
        " AND mb.name LIKE CONCAT('%', #{reqVO.babyName}, '%')",
        "</if>",
        "<if test='reqVO.questionnaireTitle != null and reqVO.questionnaireTitle != \"\"'>",
        " AND q.title LIKE CONCAT('%', #{reqVO.questionnaireTitle}, '%')",
        "</if>",
        "<if test='reqVO.completedTime != null and reqVO.completedTime.length == 2'>",
        " AND qr.completed_time BETWEEN #{reqVO.completedTime[0]} AND #{reqVO.completedTime[1]}",
        "</if>",
        "<if test='reqVO.createTime != null and reqVO.createTime.length == 2'>",
        " AND qr.create_time BETWEEN #{reqVO.createTime[0]} AND #{reqVO.createTime[1]}",
        "</if>",
        "</script>"
    })
    Long selectCountWithJoin(@Param("reqVO") QuestionnaireResultPageReqVO reqVO);

}

package cn.iocoder.yudao.module.emojump.dal.mysql.assessment;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentQuestionnaireDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 测评问卷关联 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AssessmentQuestionnaireMapper extends BaseMapperX<AssessmentQuestionnaireDO> {

    /**
     * 根据测评ID查询关联的问卷列表
     *
     * @param assessmentId 测评ID
     * @return 问卷关联列表
     */
    default List<AssessmentQuestionnaireDO> selectByAssessmentId(Long assessmentId) {
        return selectList(new LambdaQueryWrapperX<AssessmentQuestionnaireDO>()
                .eq(AssessmentQuestionnaireDO::getAssessmentId, assessmentId)
                .orderByAsc(AssessmentQuestionnaireDO::getSortOrder));
    }

    /**
     * 根据问卷ID查询关联的测评列表
     *
     * @param questionnaireId 问卷ID
     * @return 测评关联列表
     */
    default List<AssessmentQuestionnaireDO> selectByQuestionnaireId(Long questionnaireId) {
        return selectList(new LambdaQueryWrapperX<AssessmentQuestionnaireDO>()
                .eq(AssessmentQuestionnaireDO::getQuestionnaireId, questionnaireId)
                .orderByAsc(AssessmentQuestionnaireDO::getSortOrder));
    }

    /**
     * 根据测评ID删除所有关联
     *
     * @param assessmentId 测评ID
     */
    default void deleteByAssessmentId(Long assessmentId) {
        delete(new LambdaQueryWrapperX<AssessmentQuestionnaireDO>()
                .eq(AssessmentQuestionnaireDO::getAssessmentId, assessmentId));
    }

    /**
     * 检查测评和问卷是否已关联
     *
     * @param assessmentId 测评ID
     * @param questionnaireId 问卷ID
     * @return 是否存在关联
     */
    default boolean existsByAssessmentIdAndQuestionnaireId(Long assessmentId, Long questionnaireId) {
        return selectCount(new LambdaQueryWrapperX<AssessmentQuestionnaireDO>()
                .eq(AssessmentQuestionnaireDO::getAssessmentId, assessmentId)
                .eq(AssessmentQuestionnaireDO::getQuestionnaireId, questionnaireId)) > 0;
    }

}

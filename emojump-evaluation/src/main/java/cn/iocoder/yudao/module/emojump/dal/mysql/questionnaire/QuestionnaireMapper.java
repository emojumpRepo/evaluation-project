package cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.vo.questionnaire.AppQuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 问卷 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface QuestionnaireMapper extends BaseMapperX<QuestionnaireDO> {

    default PageResult<QuestionnaireDO> selectPage(QuestionnairePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuestionnaireDO>()
                .likeIfPresent(QuestionnaireDO::getTitle, reqVO.getTitle())
                .eqIfPresent(QuestionnaireDO::getStatus, reqVO.getStatus())
                .eqIfPresent(QuestionnaireDO::getType, reqVO.getType())
                .eqIfPresent(QuestionnaireDO::getIsOpen, reqVO.getIsOpen())
                .betweenIfPresent(QuestionnaireDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(QuestionnaireDO::getId));
    }

    default PageResult<QuestionnaireDO> selectPage(AppQuestionnairePageReqVO reqVO) {
        LambdaQueryWrapperX<QuestionnaireDO> wrapper = new LambdaQueryWrapperX<QuestionnaireDO>()
                .eqIfPresent(QuestionnaireDO::getType, reqVO.getType());

        if (reqVO.getKeyword() != null) {
            wrapper.and(w -> w.like(QuestionnaireDO::getTitle, reqVO.getKeyword())
                    .or()
                    .like(QuestionnaireDO::getDescription, reqVO.getKeyword()));
        }

        return selectPage(reqVO, wrapper.orderByDesc(QuestionnaireDO::getId));
    }

    default PageResult<QuestionnaireDO> selectPublishedPage(QuestionnairePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuestionnaireDO>()
                .eq(QuestionnaireDO::getStatus, 1) // 已发布状态
                .eqIfPresent(QuestionnaireDO::getType, reqVO.getType())
                .orderByDesc(QuestionnaireDO::getId));
    }

    default PageResult<QuestionnaireDO> selectPublishedPage(AppQuestionnairePageReqVO reqVO) {
        LambdaQueryWrapperX<QuestionnaireDO> wrapper = new LambdaQueryWrapperX<QuestionnaireDO>()
                .eq(QuestionnaireDO::getStatus, 1) // 已发布状态
                .eqIfPresent(QuestionnaireDO::getType, reqVO.getType());

        if (reqVO.getKeyword() != null) {
            wrapper.and(w -> w.like(QuestionnaireDO::getTitle, reqVO.getKeyword())
                    .or()
                    .like(QuestionnaireDO::getDescription, reqVO.getKeyword()));
        }

        return selectPage(reqVO, wrapper.orderByDesc(QuestionnaireDO::getId));
    }

    default PageResult<QuestionnaireDO> selectPopularPage(AppQuestionnairePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuestionnaireDO>()
                .eq(QuestionnaireDO::getStatus, 1) // 已发布状态
                .eqIfPresent(QuestionnaireDO::getType, reqVO.getType())
                .orderByDesc(QuestionnaireDO::getAccessCount)); // 按访问次数排序
    }

    default PageResult<QuestionnaireDO> searchQuestionnaire(String keyword, AppQuestionnairePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<QuestionnaireDO>()
                .eq(QuestionnaireDO::getStatus, 1) // 已发布状态
                .and(wrapper -> wrapper
                        .like(QuestionnaireDO::getTitle, keyword)
                        .or()
                        .like(QuestionnaireDO::getDescription, keyword))
                .orderByDesc(QuestionnaireDO::getId));
    }

} 

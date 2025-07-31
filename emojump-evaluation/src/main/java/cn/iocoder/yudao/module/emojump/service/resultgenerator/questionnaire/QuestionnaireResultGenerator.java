package cn.iocoder.yudao.module.emojump.service.resultgenerator.questionnaire;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireResultDTO;

import java.util.List;

/**
 * 问卷结果生成器接口
 * 
 * @author 芋道源码
 */
public interface QuestionnaireResultGenerator {

    /**
     * 获取支持的问卷ID列表
     *
     * @return 问卷ID列表
     */
    List<Long> getSupportedQuestionnaireIds();

    /**
     * 获取支持的问卷ID（兼容旧版本，返回第一个）
     *
     * @return 问卷ID
     */
    default Long getSupportedQuestionnaireId() {
        List<Long> ids = getSupportedQuestionnaireIds();
        return ids != null && !ids.isEmpty() ? ids.get(0) : null;
    }

    /**
     * 获取问卷名称（用于日志和调试）
     *
     * @return 问卷名称
     */
    String getQuestionnaireName();

    /**
     * 生成问卷结果
     *
     * @param answerDTO 问卷答案数据
     * @return 问卷结果（包含resultData和report）
     */
    QuestionnaireResultDTO generateResult(QuestionnaireAnswerDTO answerDTO);

    /**
     * 验证问卷答案数据是否有效
     *
     * @param answerDTO 问卷答案数据
     * @return 是否有效
     */
    default boolean validateAnswerData(QuestionnaireAnswerDTO answerDTO) {
        return answerDTO != null && 
               answerDTO.getAnswers() != null && 
               !answerDTO.getAnswers().isEmpty();
    }
}

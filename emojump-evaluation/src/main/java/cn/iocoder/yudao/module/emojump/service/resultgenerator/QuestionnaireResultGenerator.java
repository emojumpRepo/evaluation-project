package cn.iocoder.yudao.module.emojump.service.resultgenerator;

import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireAnswerDTO;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.QuestionnaireResultDTO;

/**
 * 问卷结果生成器接口
 * 
 * @author 芋道源码
 */
public interface QuestionnaireResultGenerator {

    /**
     * 获取支持的问卷ID
     *
     * @return 问卷ID
     */
    Long getSupportedQuestionnaireId();

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

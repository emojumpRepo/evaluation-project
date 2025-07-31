package cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 问卷答案数据传输对象
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireAnswerDTO {

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 问卷答案列表
     */
    private List<AnswerItem> answers;

    /**
     * 扩展数据（用于特殊需求）
     */
    private Map<String, Object> extraData;

    /**
     * 单个答案项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnswerItem {
        /**
         * 题目标题
         */
        private String title;

        /**
         * 答案内容
         */
        private String answer;

        /**
         * 题目序号
         */
        private Integer index;

        /**
         * 答案分值（如果有的话）
         */
        private Integer score;

        /**
         * 题目类型（单选、多选、填空等）
         */
        private String questionType;

        /**
         * 扩展属性
         */
        private Map<String, Object> properties;
    }
}

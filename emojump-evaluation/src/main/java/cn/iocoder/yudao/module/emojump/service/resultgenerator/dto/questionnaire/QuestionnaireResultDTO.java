package cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 问卷结果数据传输对象
 *
 * @author 芋道源码
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResultDTO {

    /**
     * 问卷结果数据（JSON格式）
     */
    private String resultData;

    /**
     * 问卷报告（HTML格式）
     */
    private String report;

    /**
     * 问卷得分
     */
    private BigDecimal score;

    /**
     * 问卷评级
     */
    private String level;

    /**
     * 结果摘要信息
     */
    private ResultSummary summary;

    /**
     * 详细结果列表
     */
    private List<ResultDetail> details;

    /**
     * 结果摘要
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultSummary {
        private String label;
        private Integer value;
        private String level;
        private int[] range;
        private String description;
        private String interpretation;
        private Advice advice;
    }

    /**
     * 结果详情
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultDetail {
        private String label;
        private Integer value;
        private String level;
        private int[] range;
        private String interpretation;
    }

    /**
     * 建议信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Advice {
        private String description;
        private List<String> content;
    }
}

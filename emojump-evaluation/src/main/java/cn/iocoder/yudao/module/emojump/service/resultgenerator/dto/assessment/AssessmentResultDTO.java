package cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.assessment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 测评结果数据传输对象
 *
 * @author 芋道源码
 */
@Schema(description = "测评结果数据传输对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResultDTO {

    @Schema(description = "测评结果数据（JSON格式）")
    private String resultData;

    @Schema(description = "测评报告")
    private String report;

    @Schema(description = "总体得分")
    private BigDecimal overallScore;

    @Schema(description = "总体评级")
    private String overallLevel;

    @Schema(description = "结果摘要")
    private ResultSummary summary;

    @Schema(description = "结果详情列表")
    private List<ResultDetail> details;

    @Schema(description = "问卷结果列表")
    private List<QuestionnaireResultItem> questionnaireResults;

    /**
     * 结果摘要
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultSummary {
        @Schema(description = "标签")
        private String label;
        @Schema(description = "数值")
        private Integer value;
        @Schema(description = "等级")
        private String level;
        @Schema(description = "范围")
        private int[] range;
        @Schema(description = "描述")
        private String description;
        @Schema(description = "解释")
        private String interpretation;
        @Schema(description = "建议")
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
        @Schema(description = "标签")
        private String label;
        @Schema(description = "数值")
        private Integer value;
        @Schema(description = "等级")
        private String level;
        @Schema(description = "范围")
        private int[] range;
        @Schema(description = "解释")
        private String interpretation;
    }

    /**
     * 建议
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Advice {
        @Schema(description = "描述")
        private String description;
        @Schema(description = "内容列表")
        private List<String> content;
    }

    /**
     * 问卷结果项
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionnaireResultItem {
        @Schema(description = "问卷ID")
        private Long questionnaireId;
        @Schema(description = "问卷名称")
        private String questionnaireName;
        @Schema(description = "得分")
        private BigDecimal score;
        @Schema(description = "等级")
        private String level;
        @Schema(description = "结果数据")
        private String resultData;
    }
} 
package cn.iocoder.yudao.module.emojump.controller.app.assessment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "App端 - 测评参与 Response VO")
@Data
public class AppAssessmentParticipateRespVO {

    @Schema(description = "测评结果ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long assessmentResultId;

    @Schema(description = "测评编号", example = "1024")
    private Long assessmentId;

    @Schema(description = "测评标题", example = "入学常规测评")
    private String assessmentTitle;

    @Schema(description = "问卷列表")
    private List<ParticipateQuestionnaireVO> questionnaires;

    @Schema(description = "访问令牌", example = "abc123")
    private String accessToken;

    @Schema(description = "参与问卷信息")
    @Data
    public static class ParticipateQuestionnaireVO {

        @Schema(description = "问卷ID", example = "1024")
        private Long questionnaireId;

        @Schema(description = "问卷标题", example = "焦虑量表")
        private String questionnaireTitle;

        @Schema(description = "问卷链接", example = "https://example.com/questionnaire/1024")
        private String questionnaireLink;

        @Schema(description = "排序顺序", example = "1")
        private Integer sortOrder;

        @Schema(description = "是否必填", example = "true")
        private Boolean isRequired;

        @Schema(description = "预计时长（分钟）", example = "10")
        private Integer estimatedDuration;
    }

}

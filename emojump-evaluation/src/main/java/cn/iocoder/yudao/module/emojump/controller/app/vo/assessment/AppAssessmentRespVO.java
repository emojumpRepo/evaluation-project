package cn.iocoder.yudao.module.emojump.controller.app.vo.assessment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "App端 - 测评 Response VO")
@Data
public class AppAssessmentRespVO {

    @Schema(description = "测评编号", example = "1024")
    private Long id;

    @Schema(description = "测评标题", example = "儿童发展测评")
    private String title;

    @Schema(description = "测评描述", example = "这是一个关于儿童发展的测评")
    private String description;

    @Schema(description = "测评类型", example = "1")
    private Integer type;

    @Schema(description = "测评状态", example = "1")
    private Integer status;

    @Schema(description = "目标人群", example = "3-6岁儿童")
    private String targetAudience;

    @Schema(description = "测评时长（分钟）", example = "30")
    private Integer duration;

    @Schema(description = "开始时间", example = "2024-01-01 10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime endTime;

    @Schema(description = "是否需要预约", example = "true")
    private Boolean needAppointment;

    @Schema(description = "最大参与人数", example = "100")
    private Integer maxParticipants;

    @Schema(description = "当前参与人数", example = "50")
    private Integer currentParticipants;

    @Schema(description = "问卷列表")
    private List<AppAssessmentQuestionnaireRespVO> questionnaires;

    @Schema(description = "是否已参与", example = "true")
    private Boolean isParticipated;

    @Schema(description = "参与时间", example = "2024-01-01 10:00:00")
    private LocalDateTime participateTime;

    @Schema(description = "App端测评问卷信息")
    @Data
    public static class AppAssessmentQuestionnaireRespVO {

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

        @Schema(description = "是否已完成", example = "false")
        private Boolean isCompleted;
    }

}

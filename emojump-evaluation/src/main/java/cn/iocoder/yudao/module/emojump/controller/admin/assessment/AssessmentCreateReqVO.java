package cn.iocoder.yudao.module.emojump.controller.admin.assessment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 测评创建 Request VO")
@Data
public class AssessmentCreateReqVO {

    @Schema(description = "测评标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "儿童发展测评")
    @NotEmpty(message = "测评标题不能为空")
    private String title;

    @Schema(description = "测评描述", example = "这是一个关于儿童发展的测评")
    private String description;

    @Schema(description = "问卷列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "问卷列表不能为空")
    @Valid
    private List<AssessmentQuestionnaireReqVO> questionnaires;

    @Schema(description = "测评类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "测评类型不能为空")
    private Integer type;

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

    @Schema(description = "是否可以重复测评", example = "true")
    private Boolean isRepeatable;

    @Schema(description = "最大参与人数", example = "100")
    private Integer maxParticipants;

    @Schema(description = "备注", example = "这是测评的备注信息")
    private String remark;

    @Schema(description = "测评问卷配置")
    @Data
    public static class AssessmentQuestionnaireReqVO {

        @Schema(description = "问卷ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        @NotNull(message = "问卷ID不能为空")
        private Long questionnaireId;

        @Schema(description = "排序顺序", example = "1")
        private Integer sortOrder;

        @Schema(description = "是否必填", example = "true")
        private Boolean isRequired = true;

        @Schema(description = "权重", example = "0.5")
        private BigDecimal weight = BigDecimal.ONE;
    }
}

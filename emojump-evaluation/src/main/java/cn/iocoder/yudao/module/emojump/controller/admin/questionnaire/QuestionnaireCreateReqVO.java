package cn.iocoder.yudao.module.emojump.controller.admin.questionnaire;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 问卷创建 Request VO")
@Data
public class QuestionnaireCreateReqVO {

    @Schema(description = "问卷标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "儿童发展问卷")
    @NotEmpty(message = "问卷标题不能为空")
    private String title;

    @Schema(description = "问卷描述", example = "这是一个关于儿童发展的问卷")
    private String description;

    @Schema(description = "问卷链接", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/questionnaire/1024")
    @NotEmpty(message = "问卷链接不能为空")
    private String link;

    @Schema(description = "问卷类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "问卷类型不能为空")
    private Integer type;

    @Schema(description = "目标人群", example = "3-6岁儿童")
    private String targetAudience;

    @Schema(description = "预计时长（分钟）", example = "15")
    private Integer estimatedDuration;

    @Schema(description = "是否开放", example = "true")
    private Boolean isOpen;

    @Schema(description = "有效期开始时间", example = "2024-01-01 10:00:00")
    private LocalDateTime validFrom;

    @Schema(description = "有效期结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime validTo;

    @Schema(description = "备注", example = "这是问卷的备注信息")
    private String remark;

} 

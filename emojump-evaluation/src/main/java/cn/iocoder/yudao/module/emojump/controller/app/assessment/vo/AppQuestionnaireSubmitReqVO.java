package cn.iocoder.yudao.module.emojump.controller.app.assessment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "App端 - 问卷结果提交 Request VO")
@Data
public class AppQuestionnaireSubmitReqVO {

    @Schema(description = "测评结果ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "测评结果ID不能为空")
    private Long assessmentResultId;

    @Schema(description = "问卷ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @Schema(description = "问卷结果数据（JSON格式）", example = "{\"question1\":\"A\"}")
    private String resultData;

    @Schema(description = "问卷得分", example = "85.5")
    private BigDecimal score;

    @Schema(description = "问卷评级", example = "良好")
    private String level;

    @Schema(description = "问卷报告", example = "您的焦虑水平正常")
    private String report;

    @Schema(description = "完成时��", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "完成时间不能为空")
    private LocalDateTime completedTime;

}

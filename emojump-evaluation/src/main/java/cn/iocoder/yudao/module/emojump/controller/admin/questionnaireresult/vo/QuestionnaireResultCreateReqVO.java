package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷结果创建 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 问卷结果创建 Request VO")
@Data
public class QuestionnaireResultCreateReqVO {

    @Schema(description = "测评结果ID", example = "1024")
    private Long assessmentResultId;

    @Schema(description = "测评ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "512")
    @NotNull(message = "测评ID不能为空")
    private Long assessmentId;

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "256")
    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

    @Schema(description = "问卷ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @Schema(description = "问卷结果数据（JSON格式）", example = "{\"dimension1\": 85, \"dimension2\": 92}")
    private String resultData;

    @Schema(description = "用户填写的答案数据（JSON格式）", example = "{\"q1\": \"A\", \"q2\": \"B\"}")
    private String answerData;

    @Schema(description = "问卷得分", example = "88.5")
    private BigDecimal score;

    @Schema(description = "问卷评级", example = "良好")
    private String level;

    @Schema(description = "问卷报告", example = "您的心理健康状况良好，建议继续保持...")
    private String report;

    @Schema(description = "完成时间")
    private LocalDateTime completedTime;

}

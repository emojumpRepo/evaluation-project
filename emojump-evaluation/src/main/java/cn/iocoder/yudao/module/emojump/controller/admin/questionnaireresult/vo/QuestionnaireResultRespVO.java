package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷结果 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 问卷结果 Response VO")
@Data
public class QuestionnaireResultRespVO {

    @Schema(description = "问卷结果编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "测评结果ID", example = "1024")
    private Long assessmentResultId;

    @Schema(description = "测评ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "512")
    private Long assessmentId;

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "256")
    private Long babyId;

    @Schema(description = "问卷ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
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

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime updateTime;

    // 扩展字段：问卷标题（用于显示）
    @Schema(description = "问卷标题", example = "心理健康评估问卷")
    private String questionnaireTitle;

}

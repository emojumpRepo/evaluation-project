package cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 问卷结果 Response VO")
@Data
public class QuestionnaireResultRespVO {

    @Schema(description = "问卷结果编号", required = true, example = "1")
    private Long id;

    @Schema(description = "问卷ID", required = true, example = "1024")
    private Long questionnaireId;

    @Schema(description = "问卷标题", example = "焦虑量表")
    private String questionnaireTitle;

    @Schema(description = "问卷结果数据（JSON格式）")
    private String resultData;

    @Schema(description = "用户填写的答案数据（JSON格式）")
    private String answerData;

    @Schema(description = "问卷得分", example = "90")
    private BigDecimal score;

    @Schema(description = "问卷评级", example = "正常")
    private String level;

    @Schema(description = "问卷报告")
    private String report;

    @Schema(description = "��成时间")
    private LocalDateTime completedTime;
}

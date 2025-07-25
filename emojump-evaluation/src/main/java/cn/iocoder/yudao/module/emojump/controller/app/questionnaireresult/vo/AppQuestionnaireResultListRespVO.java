package cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "App端 - 问卷测评结果列表 Response VO")
@Data
public class AppQuestionnaireResultListRespVO {

    @Schema(description = "问卷结果编号", example = "1")
    private Long id;

    @Schema(description = "问卷编号", example = "1")
    private Long questionnaireId;

    @Schema(description = "问卷得分", example = "85.5")
    private Double score;

    @Schema(description = "问卷评级", example = "优秀")
    private String level;

    @Schema(description = "完成时间", example = "2024-01-15 10:30:00")
    private LocalDateTime completedTime;

    @Schema(description = "问卷标题", example = "焦虑量表")
    private String title;
} 
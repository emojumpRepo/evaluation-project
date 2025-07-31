package cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "App端 - 问卷测评结果")
@Data
public class AppQuestionnaireResultVO {

    @Schema(description = "问卷结果编号", example = "1")
    private Long id;

    @Schema(description = "测评结果编号", example = "1")
    private Long assessmentResultId;

    @Schema(description = "测评编号", example = "1")
    private Long assessmentId;

    @Schema(description = "宝宝编号", example = "1")
    private Long babyId;

    @Schema(description = "问卷编号", example = "1")
    private Long questionnaireId;

    @Schema(description = "问卷得分", example = "85.5")
    private Double score;

    @Schema(description = "问卷评级", example = "优秀")
    private String level;

    @Schema(description = "完成时间", example = "2024-01-15 10:30:00")
    private LocalDateTime completedTime;

    @Schema(description = "结果数据", example = "{\"score\": 85.5, \"level\": \"优秀\"}")
    private String resultData;

    @Schema(description = "问卷答案", example = "{\"question1\": \"A\", \"question2\": \"B\", \"question3\": \"C\"}")
    private String answer;

    @Schema(description = "问卷报告", example = "这是一个关于儿童发展的问卷")
    private String report;
    
} 
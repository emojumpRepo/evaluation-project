package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 历史测评结果 Response VO")
@Data
public class HistoryAssessmentResultRespVO {

    @Schema(description = "结果编号", required = true, example = "1")
    private Long id;

    @Schema(description = "测评ID", required = true, example = "1024")
    private Long assessmentId;

    @Schema(description = "测评标题", example = "入学常规测评")
    private String assessmentTitle;

    @Schema(description = "宝宝ID", required = true, example = "2048")
    private Long babyId;

    @Schema(description = "宝宝名称", example = "张三")
    private String babyName;

    @Schema(description = "总体得分", example = "88.5")
    private BigDecimal overallScore;

    @Schema(description = "总体评级", example = "优秀")
    private String overallLevel;

    @Schema(description = "总体测评报告")
    private String overallReport;

    @Schema(description = "完成时间")
    private LocalDateTime completedTime;

    @Schema(description = "状态：0-进行中 1-已完成", required = true, example = "1")
    private Integer status;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "问卷结果列表")
    private List<QuestionnaireResultRespVO> questionnaireResults;

} 
package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 生成测评结果 Response VO
 *
 * @author 芋道源码
 */
@Schema(description = "用户 App - 生成测评结果 Response VO")
@Data
public class GenerateAssessmentResultRespVO {

    @Schema(description = "测评结果ID", example = "1024")
    private Long assessmentResultId;

    @Schema(description = "测评ID", example = "1024")
    private Long assessmentId;

    @Schema(description = "宝宝ID", example = "2048")
    private Long babyId;

    @Schema(description = "总体得分", example = "85.5")
    private String overallScore;

    @Schema(description = "总体评级", example = "优秀")
    private String overallLevel;

    @Schema(description = "测评报告")
    private String report;

    @Schema(description = "生成状态", example = "true")
    private Boolean success;

    @Schema(description = "错误信息")
    private String errorMessage;

} 
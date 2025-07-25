package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 生成问卷结果响应 VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 生成问卷结果响应 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateResultRespVO {

    @Schema(description = "问卷ID", example = "1001")
    private Long questionnaireId;

    @Schema(description = "总分", example = "26")
    private BigDecimal score;

    @Schema(description = "评级", example = "可能抑郁")
    private String level;

    @Schema(description = "结果数据JSON", example = "{\"summary\":{...},\"details\":[...]}")
    private String resultData;

    @Schema(description = "报告内容", example = "")
    private String report;

    @Schema(description = "生成时间", example = "2023-12-01 10:30:00")
    private String generateTime;

    @Schema(description = "是否支持该问卷", example = "true")
    private Boolean supported;

    @Schema(description = "错误信息", example = "")
    private String errorMessage;

    /**
     * 创建成功响应
     */
    public static GenerateResultRespVO success(Long questionnaireId, BigDecimal score, String level, String resultData,
                                             String report, String generateTime) {
        return GenerateResultRespVO.builder()
                .questionnaireId(questionnaireId)
                .score(score)
                .level(level)
                .resultData(resultData)
                .report(report)
                .generateTime(generateTime)
                .supported(true)
                .errorMessage("")
                .build();
    }

    /**
     * 创建不支持的响应
     */
    public static GenerateResultRespVO unsupported(Long questionnaireId) {
        return GenerateResultRespVO.builder()
                .questionnaireId(questionnaireId)
                .score(BigDecimal.ZERO)
                .level("")
                .resultData("")
                .report("")
                .generateTime("")
                .supported(false)
                .errorMessage("该问卷暂不支持自动生成结果")
                .build();
    }

    /**
     * 创建错误响应
     */
    public static GenerateResultRespVO error(Long questionnaireId, String errorMessage) {
        return GenerateResultRespVO.builder()
                .questionnaireId(questionnaireId)
                .score(BigDecimal.ZERO)
                .level("")
                .resultData("")
                .report("")
                .generateTime("")
                .supported(false)
                .errorMessage(errorMessage)
                .build();
    }
}

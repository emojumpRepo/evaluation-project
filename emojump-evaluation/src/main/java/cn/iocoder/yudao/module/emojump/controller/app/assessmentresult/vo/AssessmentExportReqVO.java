package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 APP - 测评报告导出 Request VO")
@Data
public class AssessmentExportReqVO {

    @Schema(description = "测评ID（导出类型为SINGLE_ASSESSMENT_REPORT时必填）", example = "1024")
    private Long assessmentId;

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

    @Schema(description = "导出类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "导出类型不能为空")
    private Integer exportType;

    @Schema(description = "导出类型说明")
    public static class ExportType {
        public static final int SINGLE_ASSESSMENT_REPORT = 1; // 单个测评结果报告PDF
        public static final int ALL_ASSESSMENTS_PDF = 2;      // 所有测评的PDF合集
        public static final int FILES_PACKAGE = 3;            // 附件打包ZIP
        public static final int COMPLETE_PACKAGE = 4;         // 完整报告包（PDF+附件）
    }

}
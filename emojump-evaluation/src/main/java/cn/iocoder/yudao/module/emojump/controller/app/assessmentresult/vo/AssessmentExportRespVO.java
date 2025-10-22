package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "用户 APP - 测评报告导出 Response VO")
@Data
public class AssessmentExportRespVO {

    @Schema(description = "导出任务ID", example = "12345")
    private String exportTaskId;

    @Schema(description = "文件下载地址", example = "http://example.com/download/12345")
    private String downloadUrl;

    @Schema(description = "文件名", example = "宝宝测评报告_小宝_20240122.zip")
    private String fileName;

    @Schema(description = "文件大小（字节）", example = "1024000")
    private Long fileSize;

    @Schema(description = "导出状态", example = "1")
    private Integer status;

    @Schema(description = "导出状态说明", example = "1-导出中，2-导出完成，3-导出失败")
    public static class ExportStatus {
        public static final int PROCESSING = 1; // 导出中
        public static final int SUCCESS = 2;    // 导出完成
        public static final int FAILED = 3;     // 导出失败
    }

}
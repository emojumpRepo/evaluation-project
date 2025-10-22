package cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 宝宝测评附件 Response VO")
@Data
public class BabyAssessmentFileRespVO {

    @Schema(description = "附件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    private Long babyId;

    @Schema(description = "宝宝姓名", example = "小宝")
    private String babyName;

    @Schema(description = "测评ID", example = "3096")
    private Long assessmentId;

    @Schema(description = "测评标题", example = "发育测评")
    private String assessmentTitle;

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4096")
    private Long fileId;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "疫苗证明.pdf")
    private String fileName;

    @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "application/pdf")
    private String fileType;

    @Schema(description = "文件大小", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long fileSize;

    @Schema(description = "附件描述", example = "宝宝疫苗接种证明")
    private String description;

    @Schema(description = "上传管理员ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long uploadUserId;

    @Schema(description = "上传管理员名称", example = "管理员")
    private String uploadUserName;

    @Schema(description = "文件下载地址", example = "http://example.com/file/123")
    private String fileUrl;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
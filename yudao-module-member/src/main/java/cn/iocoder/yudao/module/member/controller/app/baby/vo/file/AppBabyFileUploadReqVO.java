package cn.iocoder.yudao.module.member.controller.app.baby.vo.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;

@Schema(description = "用户 APP - 宝宝附件上传 Request VO")
@Data
public class AppBabyFileUploadReqVO {

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

    @Schema(description = "测评ID", example = "2048")
    private Long assessmentId;

    @Schema(description = "文件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3096")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @Schema(description = "文件URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "http://example.com/file/123.pdf")
    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    @Schema(description = "文件名", requiredMode = Schema.RequiredMode.REQUIRED, example = "疫苗证明.pdf")
    @NotBlank(message = "文件名不能为空")
    private String fileName;

    @Schema(description = "文件类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "application/pdf")
    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    @Schema(description = "文件大小(字节)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024000")
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @Schema(description = "附件描述", example = "宝宝疫苗接种证明")
    private String description;

}


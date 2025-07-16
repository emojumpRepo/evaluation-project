package cn.iocoder.yudao.module.system.controller.app.platform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 政策配置 Response VO")
@Data
public class AppPolicyRespVO {

    @Schema(description = "政策ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "政策类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "service_agreement")
    private String type;

    @Schema(description = "政策标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "服务协议")
    private String title;

    @Schema(description = "政策内容（富文本）", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0")
    private String version;

    @Schema(description = "生效时间", example = "2023-01-01 00:00:00")
    private LocalDateTime effectiveTime;

}

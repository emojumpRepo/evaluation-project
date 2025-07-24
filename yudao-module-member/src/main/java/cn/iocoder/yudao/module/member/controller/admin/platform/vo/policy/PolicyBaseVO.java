package cn.iocoder.yudao.module.member.controller.admin.platform.vo.policy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 政策配置 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class PolicyBaseVO {

    @Schema(description = "政策类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "service_agreement")
    @NotBlank(message = "政策类型不能为空")
    @Length(max = 50, message = "政策类型长度不能超过50个字符")
    private String type;

    @Schema(description = "政策标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "服务协议")
    @NotBlank(message = "政策标题不能为空")
    @Length(max = 100, message = "政策标题长度不能超过100个字符")
    private String title;

    @Schema(description = "政策内容（富文本）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "政策内容不能为空")
    private String content;

    @Schema(description = "版本号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1.0")
    @NotBlank(message = "版本号不能为空")
    @Length(max = 20, message = "版本号长度不能超过20个字符")
    private String version;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "生效时间", example = "2023-01-01 00:00:00")
    private LocalDateTime effectiveTime;

    @Schema(description = "备注", example = "服务协议说明")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

}

package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 检查测评结果是否全部完成 Request VO")
@Data
public class CheckCompletedReqVO {

    @Schema(description = "测评ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "测评ID不能为空")
    private Long assessmentId;

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

}
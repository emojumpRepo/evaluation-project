package cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 生成测评结果 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 生成测评结果 Request VO")
@Data
public class GenerateAssessmentResultReqVO {

    @Schema(description = "测评ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "测评ID不能为空")
    private Long assessmentId;

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

} 
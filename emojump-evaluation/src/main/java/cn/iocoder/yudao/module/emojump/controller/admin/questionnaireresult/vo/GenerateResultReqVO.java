package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 生成问卷结果请求 VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 生成问卷结果请求 VO")
@Data
public class GenerateResultReqVO {

    @Schema(description = "问卷ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotNull(message = "问卷ID不能为空")
    private Long questionnaireId;

    @Schema(description = "问卷结果ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "2001")
    @NotNull(message = "问卷结果ID不能为空")
    private Long questionnaireResultId;

}

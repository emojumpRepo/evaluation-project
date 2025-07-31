package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * 问卷结果更新 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 问卷结果更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuestionnaireResultUpdateReqVO extends QuestionnaireResultCreateReqVO {

    @Schema(description = "问卷结果编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "问卷结果编号不能为空")
    private Long id;

}

package cn.iocoder.yudao.module.emojump.controller.admin.vo.questionnaire;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 问卷更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuestionnaireUpdateReqVO extends QuestionnaireCreateReqVO {

    @Schema(description = "问卷编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "问卷编号不能为空")
    private Long id;

} 

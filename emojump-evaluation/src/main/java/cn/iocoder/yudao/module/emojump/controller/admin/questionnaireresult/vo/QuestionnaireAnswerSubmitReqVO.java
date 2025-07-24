package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 问卷答案提交 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 问卷答案提交 Request VO")
@Data
public class QuestionnaireAnswerSubmitReqVO {

    @Schema(description = "加密的用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "encrypted_user_id")
    @NotBlank(message = "用户ID不能为空")
    private String encryptedUserId;

    @Schema(description = "加密的测评ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "encrypted_assessment_id")
    @NotBlank(message = "测评ID不能为空")
    private String encryptedAssessmentId;

    @Schema(description = "加密的问卷ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "encrypted_questionnaire_id")
    @NotBlank(message = "问卷ID不能为空")
    private String encryptedQuestionnaireId;

    @Schema(description = "加密的问卷答案数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "encrypted_answer_data")
    @NotBlank(message = "问卷答案数据不能为空")
    private String encryptedAnswerData;

    @Schema(description = "完成时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "完成时间不能为空")
    private LocalDateTime completedTime;

}

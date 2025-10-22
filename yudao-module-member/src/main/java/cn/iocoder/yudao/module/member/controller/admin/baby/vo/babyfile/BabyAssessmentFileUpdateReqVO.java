package cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile;

import lombok.*;

import javax.validation.constraints.*;

/**
 * 宝宝测评附件更新 Request VO
 */
@Data
public class BabyAssessmentFileUpdateReqVO {

    @NotNull(message = "附件ID不能为空")
    private Long id;

    private Long assessmentId;

    @Size(max = 500, message = "附件描述长度不能超过500个字符")
    private String description;

}
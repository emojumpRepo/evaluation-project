package cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile;

import lombok.*;

import javax.validation.constraints.*;

/**
 * 宝宝测评附件创建 Request VO
 */
@Data
public class BabyAssessmentFileCreateReqVO {

    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

    private Long assessmentId;

    private Long fileId; // 可以为空，如果为空则从fileUrl中获取

    @NotBlank(message = "文件名不能为空")
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;

    @NotBlank(message = "文件类型不能为空")
    @Size(max = 50, message = "文件类型长度不能超过50个字符")
    private String fileType;

    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    @Size(max = 500, message = "附件描述长度不能超过500个字符")
    private String description;

    private String fileUrl; // 文件URL，用于查询fileId

}
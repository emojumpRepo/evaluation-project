package cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 宝宝测评附件分页 Request VO
 */
@Data
public class BabyAssessmentFilePageReqVO extends PageParam {

    @Schema(description = "宝宝ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long babyId;

    @Schema(description = "测评ID", example = "2048")
    private Long assessmentId;

    @Schema(description = "文件名", example = "疫苗证明.pdf")
    private String fileName;

    @Schema(description = "文件类型", example = "application/pdf")
    private String fileType;

    @Schema(description = "上传管理员ID", example = "1")
    private Long uploadUserId;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}
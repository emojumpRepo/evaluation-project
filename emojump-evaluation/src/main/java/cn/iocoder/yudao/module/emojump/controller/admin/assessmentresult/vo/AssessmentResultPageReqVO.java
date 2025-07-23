package cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 测评结果分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AssessmentResultPageReqVO extends PageParam {

    @Schema(description = "测评ID", example = "1024")
    private Long assessmentId;

    @Schema(description = "宝宝ID", example = "2048")
    private Long babyId;

    @Schema(description = "状态：0-进行中 1-已完成", example = "1")
    private Integer status;

    @Schema(description = "完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] completedTime;

}

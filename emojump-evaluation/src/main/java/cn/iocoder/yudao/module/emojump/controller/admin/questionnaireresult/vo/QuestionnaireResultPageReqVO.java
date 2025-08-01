package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

/**
 * 问卷结果分页 Request VO
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - 问卷结果分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuestionnaireResultPageReqVO extends PageParam {

    @Schema(description = "测评结果ID", example = "1024")
    private Long assessmentResultId;

    @Schema(description = "测评ID", example = "512")
    private Long assessmentId;

    @Schema(description = "宝宝ID", example = "256")
    private Long babyId;

    @Schema(description = "问卷ID", example = "2048")
    private Long questionnaireId;

    @Schema(description = "问卷评级", example = "良好")
    private String level;

    @Schema(description = "完成时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] completedTime;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "测评标题", example = "心理健康测评")
    private String assessmentTitle;

    @Schema(description = "宝宝姓名", example = "小明")
    private String babyName;

    @Schema(description = "问卷标题", example = "心理健康评估问卷")
    private String questionnaireTitle;

}

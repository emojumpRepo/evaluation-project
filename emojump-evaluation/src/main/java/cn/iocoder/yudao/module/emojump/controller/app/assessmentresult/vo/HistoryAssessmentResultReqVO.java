package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Schema(description = "用户 App - 历史测评结果查询 Request VO")
@Data
public class HistoryAssessmentResultReqVO {

    @Schema(description = "测评ID", required = true, example = "1024")
    @NotNull(message = "测评ID不能为空")
    private Long assessmentId;

    @Schema(description = "宝宝ID", required = true, example = "2048")
    @NotNull(message = "宝宝ID不能为空")
    private Long babyId;

} 
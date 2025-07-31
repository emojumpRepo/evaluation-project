package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "用户 App - 用户测评记录查询 Response VO")
@Data
public class UserAssessmentRecordsRespVO {

    @Schema(description = "测评ID列表", example = "[1, 2, 3]")
    private List<Long> assessmentIds;

} 
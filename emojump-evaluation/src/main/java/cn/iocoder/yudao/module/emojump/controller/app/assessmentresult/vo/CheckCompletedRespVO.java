package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 检查测评结果是否全部完成 Response VO")
@Data
public class CheckCompletedRespVO {

    @Schema(description = "是否全部完成", example = "true")
    private Boolean isAllCompleted;

    public CheckCompletedRespVO() {
    }

    public CheckCompletedRespVO(Boolean isAllCompleted) {
        this.isAllCompleted = isAllCompleted;
    }
}
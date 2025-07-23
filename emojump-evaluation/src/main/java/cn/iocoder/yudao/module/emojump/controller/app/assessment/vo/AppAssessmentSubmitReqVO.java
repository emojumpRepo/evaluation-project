package cn.iocoder.yudao.module.emojump.controller.app.assessment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Schema(description = "App端 - 测评提交 Request VO")
@Data
public class AppAssessmentSubmitReqVO {
    
    @Schema(description = "测评编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "测评编号不能为空")
    private Long assessmentId;
    
    @Schema(description = "问卷结果", example = "测评结果数据")
    private String resultData;
    
    @Schema(description = "完成时间", example = "2024-01-01 10:00:00")
    private LocalDateTime completedTime;
    
    @Schema(description = "宝宝编号", example = "1")
    private Long babyId;
} 

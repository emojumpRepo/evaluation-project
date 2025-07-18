package cn.iocoder.yudao.module.emojump.controller.app.assessment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "App端 - 测评结果 Response VO")
@Data
public class AppAssessmentResultRespVO {
    
    @Schema(description = "测评编号", example = "1024")
    private Long assessmentId;
    
    @Schema(description = "测评标题", example = "儿童发展测评")
    private String title;
    
    @Schema(description = "测评结果", example = "测评结果数据")
    private String resultData;
    
    @Schema(description = "完成时间", example = "2024-01-01 10:00:00")
    private LocalDateTime completedTime;
    
    @Schema(description = "评估报告", example = "您的宝宝发展良好")
    private String report;
    
} 

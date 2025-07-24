package cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "App端 - 宝宝问卷测评结果 Response VO")
@Data
public class AppBabyQuestionnaireResultRespVO {
    
    @Schema(description = "测评编号", example = "1")
    private Long assessmentId;
    
    @Schema(description = "测评标题", example = "入学常规测评")
    private String assessmentTitle;
    
    @Schema(description = "测评包含的问卷数量", example = "3")
    private Integer questionnaireCount;
    
    @Schema(description = "问卷结果列表")
    private List<QuestionnaireResultItem> questionnaireResults;
    
    @Schema(description = "问卷结果项")
    @Data
    public static class QuestionnaireResultItem {
        
        @Schema(description = "问卷编号", example = "1")
        private Long questionnaireId;
        
        @Schema(description = "问卷标题", example = "焦虑量表")
        private String questionnaireTitle;
        
        @Schema(description = "完成时间", example = "2024-01-15 10:30:00")
        private LocalDateTime completedTime;
        
        @Schema(description = "问卷得分", example = "85.5")
        private Double score;
        
        @Schema(description = "问卷评级", example = "优秀")
        private String grade;
    }
}
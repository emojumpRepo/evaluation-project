package cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "App端 - 问卷 Response VO")
@Data
public class AppQuestionnaireRespVO {
    
    @Schema(description = "问卷编号", example = "1024")
    private Long id;
    
    @Schema(description = "问卷标题", example = "儿童发展问卷")
    private String title;
    
    @Schema(description = "问卷描述", example = "这是一个关于儿童发展的问卷")
    private String description;
    
    @Schema(description = "问卷链接", example = "https://example.com/questionnaire/1024")
    private String link;
    
    @Schema(description = "预计时长（分钟）", example = "15")
    private Integer estimatedDuration;
    
    @Schema(description = "访问次数", example = "100")
    private Integer accessCount;
    
    @Schema(description = "是否热门", example = "true")
    private Boolean isPopular;
    
} 

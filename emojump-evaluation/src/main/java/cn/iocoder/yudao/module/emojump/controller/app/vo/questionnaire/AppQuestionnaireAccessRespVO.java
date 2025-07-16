package cn.iocoder.yudao.module.emojump.controller.app.vo.questionnaire;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "App端 - 问卷访问 Response VO")
@Data
public class AppQuestionnaireAccessRespVO {
    
    @Schema(description = "问卷编号", example = "1024")
    private Long id;
    
    @Schema(description = "问卷链接", example = "https://example.com/questionnaire/1024")
    private String link;
    
    @Schema(description = "访问令牌", example = "abc123")
    private String accessToken;
    
    @Schema(description = "有效期", example = "2024-01-01 10:00:00")
    private LocalDateTime expireTime;
    
} 

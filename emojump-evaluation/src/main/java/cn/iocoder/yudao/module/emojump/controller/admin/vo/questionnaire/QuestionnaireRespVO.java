package cn.iocoder.yudao.module.emojump.controller.admin.vo.questionnaire;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 问卷 Response VO")
@Data
public class QuestionnaireRespVO {

    @Schema(description = "问卷编号", example = "1024")
    private Long id;

    @Schema(description = "问卷标题", example = "儿童发展问卷")
    private String title;

    @Schema(description = "问卷描述", example = "这是一个关于儿童发展的问卷")
    private String description;

    @Schema(description = "问卷链接", example = "https://example.com/questionnaire/1024")
    private String link;

    @Schema(description = "问卷类型", example = "1")
    private Integer type;

    @Schema(description = "问卷状态", example = "1")
    private Integer status;

    @Schema(description = "目标人群", example = "3-6岁儿童")
    private String targetAudience;

    @Schema(description = "预计时长（分钟）", example = "15")
    private Integer estimatedDuration;

    @Schema(description = "访问次数", example = "100")
    private Integer accessCount;

    @Schema(description = "完成次数", example = "85")
    private Integer completionCount;

    @Schema(description = "是否开放", example = "true")
    private Boolean isOpen;

    @Schema(description = "有效期开始时间", example = "2024-01-01 10:00:00")
    private LocalDateTime validFrom;

    @Schema(description = "有效期结束时间", example = "2024-12-31 23:59:59")
    private LocalDateTime validTo;

    @Schema(description = "备注", example = "这是问卷的备注信息")
    private String remark;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "创建者", example = "admin")
    private String creator;

} 

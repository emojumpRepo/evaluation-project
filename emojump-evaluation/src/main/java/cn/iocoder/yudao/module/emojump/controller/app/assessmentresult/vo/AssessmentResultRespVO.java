package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "用户 App - 测评结果 Response VO")
@Data
public class AssessmentResultRespVO {

    @Schema(description = "测评结果ID", example = "1")
    private Long id;

    @Schema(description = "测评ID", example = "1024")
    private Long assessmentId;

    @Schema(description = "宝宝ID", example = "2048")
    private Long babyId;

    @Schema(description = "测评名称", example = "儿童发育测评")
    private String assessmentName;

    @Schema(description = "宝宝姓名", example = "小明")
    private String babyName;

    @Schema(description = "宝宝性别", example = "男")
    private String babyGender;

    @Schema(description = "宝宝生日", example = "2020-01-01")
    private String babyBirthday;

    @Schema(description = "总体得分", example = "85.5")
    private BigDecimal overallScore;

    @Schema(description = "发育等级", example = "正常")
    private String developmentLevel;

    @Schema(description = "测评报告", example = "JSON格式的测评报告")
    private String report;

    @Schema(description = "完成时间", example = "2024-01-01 10:00:00")
    private LocalDateTime completedTime;

    @Schema(description = "创建时间", example = "2024-01-01 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "问卷结果列表")
    private List<QuestionnaireResultRespVO> questionnaireResults;

    @Schema(description = "是否存在最新记录", example = "true")
    private Boolean hasLatestRecord;
} 
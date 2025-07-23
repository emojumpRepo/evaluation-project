package cn.iocoder.yudao.module.emojump.controller.app.assessment.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "App端 - 测评分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppAssessmentPageReqVO extends PageParam {
    
    @Schema(description = "测评类型", example = "1")
    private Integer type;
    
    @Schema(description = "测评状态", example = "1")
    private Integer status;

    // 获得宝宝的测评列表需要
    @Schema(description = "宝宝编号", example = "1")
    private Long babyId;
} 

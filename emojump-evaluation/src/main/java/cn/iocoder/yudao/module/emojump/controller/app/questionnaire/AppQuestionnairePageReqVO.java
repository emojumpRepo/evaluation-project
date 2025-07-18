package cn.iocoder.yudao.module.emojump.controller.app.vo.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "App端 - 问卷分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AppQuestionnairePageReqVO extends PageParam {
    
    @Schema(description = "问卷类型", example = "1")
    private Integer type;
    
    @Schema(description = "关键字", example = "儿童")
    private String keyword;
    
} 

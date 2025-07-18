package cn.iocoder.yudao.module.emojump.controller.admin.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 测评分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AssessmentPageReqVO extends PageParam {

    @Schema(description = "测评标题", example = "儿童发展测评")
    private String title;

    @Schema(description = "测评状态", example = "1")
    private Integer status;

    @Schema(description = "测评类型", example = "1")
    private Integer type;

    @Schema(description = "问卷ID", example = "1024")
    private Long questionnaireId;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

} 

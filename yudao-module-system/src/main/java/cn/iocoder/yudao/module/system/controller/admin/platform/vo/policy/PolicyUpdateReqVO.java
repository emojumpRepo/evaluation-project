package cn.iocoder.yudao.module.system.controller.admin.platform.vo.policy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 政策配置更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PolicyUpdateReqVO extends PolicyBaseVO {

    @Schema(description = "政策ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "政策ID不能为空")
    private Long id;

}

package cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 轮播图更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CarouselUpdateReqVO extends CarouselBaseVO {

    @Schema(description = "轮播图ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "轮播图ID不能为空")
    private Long id;

}

package cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 轮播图 Base VO，提供给添加、修改、详细的子 VO 使用
 * 如果子 VO 存在差异的字段，请不要添加到这里，影响 Swagger 文档生成
 */
@Data
public class CarouselBaseVO {

    @Schema(description = "轮播图标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "春节活动")
    @NotBlank(message = "轮播图标题不能为空")
    @Length(max = 100, message = "轮播图标题长度不能超过100个字符")
    private String title;

    @Schema(description = "图片URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xx.png")
    @NotBlank(message = "图片URL不能为空")
    @Length(max = 500, message = "图片URL长度不能超过500个字符")
    private String imageUrl;

    @Schema(description = "跳转链接", example = "https://www.iocoder.cn")
    @Length(max = 500, message = "跳转链接长度不能超过500个字符")
    private String linkUrl;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "备注", example = "春节活动轮播图")
    @Length(max = 500, message = "备注长度不能超过500个字符")
    private String remark;

}

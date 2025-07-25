package cn.iocoder.yudao.module.system.controller.app.platform.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 APP - 轮播图 Response VO")
@Data
public class AppCarouselRespVO {

    @Schema(description = "轮播图ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "轮播图标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "春节活动")
    private String title;

    @Schema(description = "副标题", example = "活动详情")
    private String subtitle;

    @Schema(description = "图片URL", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xx.png")
    private String imageUrl;

    @Schema(description = "跳转链接", example = "https://www.iocoder.cn")
    private String linkUrl;

    @Schema(description = "排序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sort;

    @Schema(description = "类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer type;

    @Schema(description = "弹窗内容", example = "这是一个弹窗")
    private String popupContent;

}

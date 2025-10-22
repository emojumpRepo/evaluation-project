package cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 文章分类精简 Response VO")
@Data
public class ArticleCategorySimpleRespVO {

    @Schema(description = "分类ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "分类名称", required = true, example = "技术分享")
    private String name;

}


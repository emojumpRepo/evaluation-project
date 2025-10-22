package cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 文章分类更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArticleCategoryUpdateReqVO extends ArticleCategoryBaseVO {

    @Schema(description = "分类ID", required = true, example = "1024")
    @NotNull(message = "分类ID不能为空")
    private Long id;

}


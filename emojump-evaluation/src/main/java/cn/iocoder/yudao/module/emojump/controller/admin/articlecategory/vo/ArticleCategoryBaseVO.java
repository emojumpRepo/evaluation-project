package cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 文章分类 Base VO，提供给添加、修改、详细的子 VO 使用
 *
 * @author 芋道源码
 */
@Data
public class ArticleCategoryBaseVO {

    @Schema(description = "分类名称", required = true, example = "技术分享")
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(description = "排序", required = true, example = "1")
    @NotNull(message = "排序不能为空")
    private Integer sort;

}


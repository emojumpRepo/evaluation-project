package cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 文章分类分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArticleCategoryPageReqVO extends PageParam {

    @Schema(description = "分类名称", example = "技术分享")
    private String name;

}


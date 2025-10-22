package cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文章分类 Response VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArticleCategoryRespVO extends ArticleCategoryBaseVO {

    @Schema(description = "分类ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime;

}


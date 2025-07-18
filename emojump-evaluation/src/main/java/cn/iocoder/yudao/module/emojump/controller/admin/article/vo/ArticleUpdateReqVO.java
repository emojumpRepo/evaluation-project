package cn.iocoder.yudao.module.emojump.controller.admin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 文章更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ArticleUpdateReqVO extends ArticleCreateReqVO {

    @Schema(description = "文章ID", required = true, example = "1024")
    @NotNull(message = "文章ID不能为空")
    private Long id;

} 
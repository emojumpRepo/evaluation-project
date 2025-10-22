package cn.iocoder.yudao.module.emojump.controller.app.article.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "用户 APP - 会员文章分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppArticlePageReqVO extends PageParam {

    @Schema(description = "文章标题", example = "Spring Boot")
    private String title;

    @Schema(description = "文章分类ID", example = "1")
    private Long categoryId;

} 
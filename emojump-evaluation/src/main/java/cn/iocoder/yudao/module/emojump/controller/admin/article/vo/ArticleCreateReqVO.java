package cn.iocoder.yudao.module.emojump.controller.admin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema(description = "管理后台 - 会员文章创建 Request VO")
@Data
public class ArticleCreateReqVO {

    @Schema(description = "文章标题", required = true, example = "Spring Boot 入门教程")
    @NotBlank(message = "文章标题不能为空")
    private String title;

    @Schema(description = "文章内容", required = true)
    @NotBlank(message = "文章内容不能为空")
    private String content;

    @Schema(description = "文章封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "文章类别", example = "技术文章")
    private String category;

    @Schema(description = "文章状态", required = true, example = "1")
    @NotNull(message = "文章状态不能为空")
    private Integer status;

    @Schema(description = "发布时间（时间戳）", example = "1640995200000")
    private Long publishTime;

    @Schema(description = "备注", example = "这是一篇很棒的文章")
    private String remark;

} 
package cn.iocoder.yudao.module.emojump.controller.admin.article.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 文章 Response VO")
@Data
public class ArticleRespVO {

    @Schema(description = "文章ID", required = true, example = "1024")
    private Long id;

    @Schema(description = "文章标题", required = true, example = "Spring Boot 入门教程")
    private String title;

    @Schema(description = "文章内容", required = true)
    private String content;

    @Schema(description = "文章封面图片URL", example = "https://example.com/cover.jpg")
    private String coverImage;

    @Schema(description = "文章分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "文章状态", required = true, example = "1")
    private Integer status;

    @Schema(description = "阅读量", example = "100")
    private Integer viewCount;

    @Schema(description = "点赞数", example = "10")
    private Integer likeCount;

    @Schema(description = "发布时间（时间戳）", example = "1640995200000")
    private Long publishTime;

    @Schema(description = "备注", example = "这是一篇很棒的文章")
    private String remark;

    @Schema(description = "创建时间", required = true)
    private LocalDateTime createTime;

    @Schema(description = "更新时间", required = true)
    private LocalDateTime updateTime;

} 
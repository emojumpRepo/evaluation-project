package cn.iocoder.yudao.module.emojump.controller.admin.article;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.*;
import cn.iocoder.yudao.module.emojump.convert.article.ArticleConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleDO;
import cn.iocoder.yudao.module.emojump.service.article.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 文章")
@RestController
@RequestMapping("/emojump/article")
@Validated
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @PostMapping("/create")
    @Operation(summary = "创建文章")
    // @PreAuthorize("@ss.hasPermission('member:article:create')")
    public CommonResult<Long> createArticle(@Valid @RequestBody ArticleCreateReqVO createReqVO) {
        return success(articleService.createArticle(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新文章")
    // @PreAuthorize("@ss.hasPermission('member:article:update')")
    public CommonResult<Boolean> updateArticle(@Valid @RequestBody ArticleUpdateReqVO updateReqVO) {
        articleService.updateArticle(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文章")
    @Parameter(name = "id", description = "编号", required = true)
    // @PreAuthorize("@ss.hasPermission('member:article:delete')")
    public CommonResult<Boolean> deleteArticle(@RequestParam("id") Long id) {
        articleService.deleteArticle(id);
        return success(true);
    }

    @DeleteMapping("/delete-batch")
    @Operation(summary = "批量删除文章")
    // @PreAuthorize("@ss.hasPermission('member:article:delete')")
    public CommonResult<Boolean> deleteArticles(@RequestParam("ids") List<Long> ids) {
        articleService.deleteArticles(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得文章")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    // @PreAuthorize("@ss.hasPermission('member:article:query')")
    public CommonResult<ArticleRespVO> getArticle(@RequestParam("id") Long id) {
        ArticleDO article = articleService.getArticle(id);
        return success(ArticleConvert.INSTANCE.convert(article));
    }

    @GetMapping("/page")
    @Operation(summary = "获得文章分页")
    // @PreAuthorize("@ss.hasPermission('member:article:query')")
    public CommonResult<PageResult<ArticleRespVO>> getArticlePage(@Valid ArticlePageReqVO pageVO) {
        PageResult<ArticleDO> pageResult = articleService.getArticlePage(pageVO);
        return success(ArticleConvert.INSTANCE.convertPage(pageResult));
    }

} 
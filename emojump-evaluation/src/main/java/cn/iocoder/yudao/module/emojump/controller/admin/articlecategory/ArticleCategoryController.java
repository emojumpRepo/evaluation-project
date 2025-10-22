package cn.iocoder.yudao.module.emojump.controller.admin.articlecategory;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.*;
import cn.iocoder.yudao.module.emojump.convert.articlecategory.ArticleCategoryConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleCategoryDO;
import cn.iocoder.yudao.module.emojump.service.articlecategory.ArticleCategoryService;
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

/**
 * 文章分类 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 文章分类")
@RestController
@RequestMapping("/emojump/article-category")
@Validated
public class ArticleCategoryController {

    @Resource
    private ArticleCategoryService articleCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建文章分类")
    @PreAuthorize("@ss.hasPermission('emojump:article-category:create')")
    public CommonResult<Long> createArticleCategory(@Valid @RequestBody ArticleCategoryCreateReqVO createReqVO) {
        return success(articleCategoryService.createArticleCategory(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新文章分类")
    @PreAuthorize("@ss.hasPermission('emojump:article-category:update')")
    public CommonResult<Boolean> updateArticleCategory(@Valid @RequestBody ArticleCategoryUpdateReqVO updateReqVO) {
        articleCategoryService.updateArticleCategory(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除文章分类")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:article-category:delete')")
    public CommonResult<Boolean> deleteArticleCategory(@RequestParam("id") Long id) {
        articleCategoryService.deleteArticleCategory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得文章分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emojump:article-category:query')")
    public CommonResult<ArticleCategoryRespVO> getArticleCategory(@RequestParam("id") Long id) {
        ArticleCategoryDO category = articleCategoryService.getArticleCategory(id);
        return success(ArticleCategoryConvert.INSTANCE.convert(category));
    }

    @GetMapping("/page")
    @Operation(summary = "获得文章分类分页")
    @PreAuthorize("@ss.hasPermission('emojump:article-category:query')")
    public CommonResult<PageResult<ArticleCategoryRespVO>> getArticleCategoryPage(@Valid ArticleCategoryPageReqVO pageReqVO) {
        PageResult<ArticleCategoryDO> pageResult = articleCategoryService.getArticleCategoryPage(pageReqVO);
        return success(ArticleCategoryConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/list-all-simple")
    @Operation(summary = "获取文章分类精简信息列表", description = "只包含被开启的文章分类，主要用于前端的下拉选项")
    public CommonResult<List<ArticleCategorySimpleRespVO>> getSimpleArticleCategoryList() {
        List<ArticleCategoryDO> list = articleCategoryService.getArticleCategoryListAll();
        return success(ArticleCategoryConvert.INSTANCE.convertSimpleList(list));
    }

}


package cn.iocoder.yudao.module.emojump.controller.app.articlecategory;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.controller.app.articlecategory.vo.AppArticleCategoryRespVO;
import cn.iocoder.yudao.module.emojump.convert.articlecategory.ArticleCategoryConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleCategoryDO;
import cn.iocoder.yudao.module.emojump.service.articlecategory.ArticleCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 用户 APP - 文章分类 Controller
 *
 * @author 芋道源码
 */
@Tag(name = "用户 APP - 文章分类")
@RestController
@RequestMapping("/emojump/article-category")
@Validated
@Slf4j
public class AppArticleCategoryController {

    @Resource
    private ArticleCategoryService articleCategoryService;

    @GetMapping("/list")
    @Operation(summary = "获取文章分类列表", description = "获取所有文章分类列表，供前端展示")
    @PermitAll
    public CommonResult<List<AppArticleCategoryRespVO>> getArticleCategoryList() {
        List<ArticleCategoryDO> list = articleCategoryService.getArticleCategoryListAll();
        return success(ArticleCategoryConvert.INSTANCE.convertAppList(list));
    }

}


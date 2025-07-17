package cn.iocoder.yudao.module.emojump.controller.app.article;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.app.article.vo.AppArticlePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.article.vo.AppArticleRespVO;
import cn.iocoder.yudao.module.emojump.convert.article.ArticleConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleDO;
import cn.iocoder.yudao.module.emojump.service.article.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 会员文章")
@RestController
@RequestMapping("/emojump/article")
@Validated
@Slf4j
public class AppArticleController {

    @Resource
    private ArticleService articleService;

    @GetMapping("/page")
    @Operation(summary = "获得会员文章分页")
    @PermitAll
    public CommonResult<PageResult<AppArticleRespVO>> getArticlePage(@Valid AppArticlePageReqVO pageVO) {
        PageResult<ArticleDO> pageResult = articleService.getAppArticlePage(pageVO);
        return success(ArticleConvert.INSTANCE.convertAppPage(pageResult));
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员文章详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PermitAll
    public CommonResult<AppArticleRespVO> getArticle(@RequestParam("id") Long id) {
        ArticleDO article = articleService.getAppArticle(id);
        return success(ArticleConvert.INSTANCE.convertApp(article));
    }

    @PostMapping("/like")
    @Operation(summary = "点赞文章")
    @Parameter(name = "id", description = "文章编号", required = true, example = "1024")
    public CommonResult<Boolean> likeArticle(@RequestParam("id") Long id) {
        articleService.increaseLikeCount(id);
        return success(true);
    }
} 
package cn.iocoder.yudao.module.member.controller.admin.article;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.*;
import cn.iocoder.yudao.module.member.convert.article.MemberArticleConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.article.MemberArticleDO;
import cn.iocoder.yudao.module.member.service.article.MemberArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 会员文章")
@RestController
@RequestMapping("/member/article")
@Validated
public class MemberArticleController {

    @Resource
    private MemberArticleService articleService;

    @PostMapping("/create")
    @Operation(summary = "创建会员文章")
    // @PreAuthorize("@ss.hasPermission('member:article:create')")
    public CommonResult<Long> createArticle(@Valid @RequestBody MemberArticleCreateReqVO createReqVO) {
        return success(articleService.createArticle(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会员文章")
    @PreAuthorize("@ss.hasPermission('member:article:update')")
    public CommonResult<Boolean> updateArticle(@Valid @RequestBody MemberArticleUpdateReqVO updateReqVO) {
        articleService.updateArticle(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会员文章")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:article:delete')")
    public CommonResult<Boolean> deleteArticle(@RequestParam("id") Long id) {
        articleService.deleteArticle(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员文章")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:article:query')")
    public CommonResult<MemberArticleRespVO> getArticle(@RequestParam("id") Long id) {
        MemberArticleDO article = articleService.getArticle(id);
        return success(MemberArticleConvert.INSTANCE.convert(article));
    }

    @GetMapping("/page")
    @Operation(summary = "获得会员文章分页")
    @PreAuthorize("@ss.hasPermission('member:article:query')")
    public CommonResult<PageResult<MemberArticleRespVO>> getArticlePage(@Valid MemberArticlePageReqVO pageVO) {
        PageResult<MemberArticleDO> pageResult = articleService.getArticlePage(pageVO);
        return success(MemberArticleConvert.INSTANCE.convertPage(pageResult));
    }

} 
package cn.iocoder.yudao.module.member.controller.app.article;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.app.article.vo.AppMemberArticlePageReqVO;
import cn.iocoder.yudao.module.member.controller.app.article.vo.AppMemberArticleRespVO;
import cn.iocoder.yudao.module.member.convert.article.MemberArticleConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.article.MemberArticleDO;
import cn.iocoder.yudao.module.member.service.article.MemberArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 会员文章")
@RestController
@RequestMapping("/member/article")
@Validated
@Slf4j
public class AppMemberArticleController {

    @Resource
    private MemberArticleService articleService;

    @GetMapping("/page")
    @Operation(summary = "获得会员文章分页")
    public CommonResult<PageResult<AppMemberArticleRespVO>> getArticlePage(@Valid AppMemberArticlePageReqVO pageVO) {
        PageResult<MemberArticleDO> pageResult = articleService.getAppArticlePage(pageVO);
        return success(MemberArticleConvert.INSTANCE.convertAppPage(pageResult));
    }

    @GetMapping("/get")
    @Operation(summary = "获得会员文章详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppMemberArticleRespVO> getArticle(@RequestParam("id") Long id) {
        MemberArticleDO article = articleService.getAppArticle(id);
        return success(MemberArticleConvert.INSTANCE.convertApp(article));
    }

    @PostMapping("/like")
    @Operation(summary = "点赞文章")
    @Parameter(name = "id", description = "文章编号", required = true, example = "1024")
    public CommonResult<Boolean> likeArticle(@RequestParam("id") Long id) {
        articleService.increaseLikeCount(id);
        return success(true);
    }
} 
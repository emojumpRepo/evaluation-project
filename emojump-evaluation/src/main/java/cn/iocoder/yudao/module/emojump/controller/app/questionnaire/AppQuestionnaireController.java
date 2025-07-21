package cn.iocoder.yudao.module.emojump.controller.app.questionnaire;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireAccessRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.service.questionnaire.QuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 问卷管理")
@RestController
@RequestMapping("/emojump/questionnaire")
@Validated
public class AppQuestionnaireController {

    @Resource
    private QuestionnaireService questionnaireService;

    public AppQuestionnaireController() {
        System.out.println(getClass() + "生效啦！！！");
    }

    @GetMapping("/get")
    @Operation(summary = "获得问卷信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppQuestionnaireRespVO> getQuestionnaire(@RequestParam("id") Long id) {
        return success(questionnaireService.getAppQuestionnaire(id));
    }

    @GetMapping("/published")
    @Operation(summary = "获得已发布问卷列表")
    public CommonResult<PageResult<AppQuestionnaireRespVO>> getPublishedQuestionnaireList(@Valid AppQuestionnairePageReqVO pageReqVO) {
        return success(questionnaireService.getPublishedAppQuestionnairePage(pageReqVO));
    }

    @GetMapping("/access")
    @Operation(summary = "获得问卷访问链接")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppQuestionnaireAccessRespVO> getQuestionnaireAccess(@RequestParam("id") Long id) {
        return success(questionnaireService.getQuestionnaireAccess(id));
    }

    @PostMapping("/record-access")
    @Operation(summary = "记录问卷访问")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> recordQuestionnaireAccess(@RequestParam("id") Long id) {
        questionnaireService.recordQuestionnaireAccess(id);
        return success(true);
    }

    @GetMapping("/popular")
    @Operation(summary = "获得热门问卷列表")
    public CommonResult<PageResult<AppQuestionnaireRespVO>> getPopularQuestionnaireList(@Valid AppQuestionnairePageReqVO pageReqVO) {
        return success(questionnaireService.getPopularQuestionnairePage(pageReqVO));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索问卷")
    @Parameter(name = "keyword", description = "关键字", required = true)
    public CommonResult<PageResult<AppQuestionnaireRespVO>> searchQuestionnaire(@RequestParam("keyword") String keyword, @Valid AppQuestionnairePageReqVO pageReqVO) {
        return success(questionnaireService.searchQuestionnaire(keyword, pageReqVO));
    }
} 

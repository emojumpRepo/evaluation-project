package cn.iocoder.yudao.module.emojump.controller.admin.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.service.questionnaire.QuestionnaireService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 问卷管理")
@RestController
@RequestMapping("/emojump/questionnaire")
@Validated
public class QuestionnaireController {

    @Resource
    private QuestionnaireService questionnaireService;

    public QuestionnaireController() {
        System.out.println(getClass() + "生效啦！！！");
    }

    @PostMapping("/create")
    @Operation(summary = "创建问卷")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:create')")
    public CommonResult<Long> createQuestionnaire(@Valid @RequestBody QuestionnaireCreateReqVO createReqVO) {
        return success(questionnaireService.createQuestionnaire(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新问卷")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:update')")
    public CommonResult<Boolean> updateQuestionnaire(@Valid @RequestBody QuestionnaireUpdateReqVO updateReqVO) {
        questionnaireService.updateQuestionnaire(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除问卷")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:delete')")
    public CommonResult<Boolean> deleteQuestionnaire(@RequestParam("id") Long id) {
        questionnaireService.deleteQuestionnaire(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得问卷")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:query')")
    public CommonResult<QuestionnaireRespVO> getQuestionnaire(@RequestParam("id") Long id) {
        return success(questionnaireService.getQuestionnaire(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得问卷列表")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:query')")
    public CommonResult<PageResult<QuestionnaireRespVO>> getQuestionnaireList(@Valid QuestionnairePageReqVO pageVO) {
        return success(questionnaireService.getQuestionnairePage(pageVO));
    }

    @PostMapping("/publish")
    @Operation(summary = "发布问卷")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:publish')")
    public CommonResult<Boolean> publishQuestionnaire(@RequestParam("id") Long id) {
        questionnaireService.publishQuestionnaire(id);
        return success(true);
    }

    @PostMapping("/unpublish")
    @Operation(summary = "下线问卷")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:publish')")
    public CommonResult<Boolean> unpublishQuestionnaire(@RequestParam("id") Long id) {
        questionnaireService.unpublishQuestionnaire(id);
        return success(true);
    }

    @GetMapping("/published")
    @Operation(summary = "获得已发布问卷列表")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:query')")
    public CommonResult<PageResult<QuestionnaireRespVO>> getPublishedQuestionnaireList(@Valid QuestionnairePageReqVO pageVO) {
        return success(questionnaireService.getPublishedQuestionnairePage(pageVO));
    }

    @PostMapping("/test-link")
    @Operation(summary = "测试问卷链接")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:query')")
    public CommonResult<Boolean> testQuestionnaireLink(@RequestParam("id") Long id) {
        return success(questionnaireService.testQuestionnaireLink(id));
    }
} 

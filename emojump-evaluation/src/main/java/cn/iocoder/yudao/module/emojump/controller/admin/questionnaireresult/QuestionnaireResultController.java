package cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireAnswerSubmitReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultExcelVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultUpdateReqVO;
import cn.iocoder.yudao.module.emojump.convert.questionnaireresult.QuestionnaireResultConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.service.questionnaireresult.QuestionnaireResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 问卷结果
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 问卷结果")
@RestController
@RequestMapping("/emojump/questionnaire-result")
@Validated
public class QuestionnaireResultController {

    @Resource
    private QuestionnaireResultService questionnaireResultService;

    @PostMapping("/create")
    @Operation(summary = "创建问卷结果")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:create')")
    public CommonResult<Long> createQuestionnaireResult(@Valid @RequestBody QuestionnaireResultCreateReqVO createReqVO) {
        return success(questionnaireResultService.createQuestionnaireResult(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新问卷结果")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:update')")
    public CommonResult<Boolean> updateQuestionnaireResult(@Valid @RequestBody QuestionnaireResultUpdateReqVO updateReqVO) {
        questionnaireResultService.updateQuestionnaireResult(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除问卷结果")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:delete')")
    public CommonResult<Boolean> deleteQuestionnaireResult(@RequestParam("id") Long id) {
        questionnaireResultService.deleteQuestionnaireResult(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得问卷结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:query')")
    public CommonResult<QuestionnaireResultRespVO> getQuestionnaireResult(@RequestParam("id") Long id) {
        EmoQuestionnaireResultDO questionnaireResult = questionnaireResultService.getQuestionnaireResult(id);
        return success(QuestionnaireResultConvert.INSTANCE.convert(questionnaireResult));
    }

    @GetMapping("/list")
    @Operation(summary = "获得问卷结果列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:query')")
    public CommonResult<List<QuestionnaireResultRespVO>> getQuestionnaireResultList(@RequestParam("ids") Collection<Long> ids) {
        List<EmoQuestionnaireResultDO> list = questionnaireResultService.getQuestionnaireResultList(ids);
        return success(QuestionnaireResultConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得问卷结果分页")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:query')")
    public CommonResult<PageResult<QuestionnaireResultRespVO>> getQuestionnaireResultPage(@Valid QuestionnaireResultPageReqVO pageVO) {
        PageResult<EmoQuestionnaireResultDO> pageResult = questionnaireResultService.getQuestionnaireResultPage(pageVO);
        return success(QuestionnaireResultConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出问卷结果 Excel")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:export')")
    public void exportQuestionnaireResultExcel(@Valid QuestionnaireResultPageReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<EmoQuestionnaireResultDO> list = questionnaireResultService.getQuestionnaireResultList(exportReqVO);
        // 导出 Excel
        List<QuestionnaireResultExcelVO> datas = QuestionnaireResultConvert.INSTANCE.convertList02(list);
        ExcelUtils.write(response, "问卷结果.xls", "数据", QuestionnaireResultExcelVO.class, datas);
    }

    @GetMapping("/list-by-assessment-result")
    @Operation(summary = "根据测评结果ID获取问卷结果列表")
    @Parameter(name = "assessmentResultId", description = "测评结果ID", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:query')")
    public CommonResult<List<QuestionnaireResultRespVO>> getQuestionnaireResultListByAssessmentResultId(@RequestParam("assessmentResultId") Long assessmentResultId) {
        List<EmoQuestionnaireResultDO> list = questionnaireResultService.getQuestionnaireResultListByAssessmentResultId(assessmentResultId);
        return success(QuestionnaireResultConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/list-by-questionnaire")
    @Operation(summary = "根据问卷ID获取问卷结果列表")
    @Parameter(name = "questionnaireId", description = "问卷ID", required = true, example = "2048")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:query')")
    public CommonResult<List<QuestionnaireResultRespVO>> getQuestionnaireResultListByQuestionnaireId(@RequestParam("questionnaireId") Long questionnaireId) {
        List<EmoQuestionnaireResultDO> list = questionnaireResultService.getQuestionnaireResultListByQuestionnaireId(questionnaireId);
        return success(QuestionnaireResultConvert.INSTANCE.convertList(list));
    }

    @PostMapping("/delete-batch")
    @Operation(summary = "批量删除问卷结果")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:delete')")
    public CommonResult<Boolean> deleteQuestionnaireResultBatch(@RequestBody Collection<Long> ids) {
        questionnaireResultService.deleteQuestionnaireResultBatch(ids);
        return success(true);
    }

    @PostMapping("/submit-answer")
    @Operation(summary = "提交问卷答案")
//    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:create')")
    @PermitAll
    public CommonResult<Long> submitQuestionnaireAnswer(@Valid @RequestBody QuestionnaireAnswerSubmitReqVO submitReqVO) {
        return success(questionnaireResultService.submitQuestionnaireAnswer(submitReqVO));
    }

}

package cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppBabyQuestionnaireResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultListRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultVO;
import cn.iocoder.yudao.module.emojump.service.questionnaireresult.QuestionnaireResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 问卷结果管理")
@RestController
@RequestMapping("/emojump/questionnaire-result")
@Validated
public class AppQuestionnaireResultController {

    @Resource
    private QuestionnaireResultService questionnaireResultService;

    public AppQuestionnaireResultController() {
        System.out.println(getClass() + "生效啦！！！");
    }

    @GetMapping("/baby-result-list")
    @Operation(summary = "获取宝宝的问卷测评结果")
    @Parameter(name = "babyId", description = "宝宝编号", required = true, example = "1024")
    public CommonResult<List<AppBabyQuestionnaireResultRespVO>> getBabyQuestionnaireResults(@RequestParam("babyId") Long babyId) {
        return success(questionnaireResultService.getBabyQuestionnaireResults(babyId));
    }

    @GetMapping("/history-record")
    @Operation(summary = "获取所有问卷测评结果列表")
    @Parameter(name = "babyId", description = "宝宝编号", required = true, example = "1024")
    @Parameter(name = "questionnaireId", description = "问卷编号", required = false, example = "2048")
    @Parameter(name = "assessmentId", description = "测评编号", required = false, example = "4096")
    public CommonResult<List<AppQuestionnaireResultListRespVO>> getAllResultsByBabyAndQuestionnaire(@RequestParam("babyId") Long babyId,
                                                                                                   @RequestParam(value = "questionnaireId", required = false) Long questionnaireId,
                                                                                                   @RequestParam(value = "assessmentId", required = false) Long assessmentId) {
        return success(questionnaireResultService.getAllResultsByBabyAndQuestionnaire(babyId, questionnaireId, assessmentId));
    }

    @GetMapping("/get")
    @Operation(summary = "根据ID获取问卷结果")
    @Parameter(name = "id", description = "问卷结果编号", required = true, example = "1024")
    public CommonResult<AppQuestionnaireResultVO> getQuestionnaireResultById(@RequestParam("id") Long id) {
        return success(questionnaireResultService.getQuestionnaireResultById(id));
    }
}
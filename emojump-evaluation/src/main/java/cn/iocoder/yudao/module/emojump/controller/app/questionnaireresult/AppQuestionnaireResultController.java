package cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppBabyQuestionnaireResultRespVO;
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
}
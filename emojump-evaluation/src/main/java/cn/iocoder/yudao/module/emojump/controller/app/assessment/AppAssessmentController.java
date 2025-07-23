package cn.iocoder.yudao.module.emojump.controller.app.assessment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentParticipateRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentSubmitReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppQuestionnaireSubmitReqVO;
import cn.iocoder.yudao.module.emojump.service.assessment.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 测评管理")
@RestController
@RequestMapping("/emojump/assessment")
@Validated
public class AppAssessmentController {

    @Resource
    private AssessmentService assessmentService;

    public AppAssessmentController() {
        System.out.println(getClass() + "生效啦！！！");
    }

    @GetMapping("/get")
    @Operation(summary = "获得测评信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @Parameter(name = "babyId", description = "宝宝编号", required = true, example = "1024")
    public CommonResult<AppAssessmentRespVO> getAssessment(@RequestParam("id") Long id, @RequestParam("babyId") Long babyId) {
        return success(assessmentService.getAppAssessment(id, babyId));
    }

    @GetMapping("/list")
    @Operation(summary = "获得测评列表")
    public CommonResult<PageResult<AppAssessmentRespVO>> getAssessmentList(@Valid AppAssessmentPageReqVO pageReqVO) {
        return success(assessmentService.getAppAssessmentPage(pageReqVO));
    }

    @GetMapping("/published")
    @Operation(summary = "获得已发布测评列表")
    public CommonResult<PageResult<AppAssessmentRespVO>> getPublishedAssessmentList(@Valid AppAssessmentPageReqVO pageReqVO) {
        return success(assessmentService.getPublishedAssessmentPage(pageReqVO));
    }

    @PostMapping("/participate")
    @Operation(summary = "参与测评")
    @Parameters({
        @Parameter(name = "assessmentId", description = "测评编号", required = true),
        @Parameter(name = "babyId", description = "宝宝编号", required = true)
    })
    public CommonResult<AppAssessmentParticipateRespVO> participateAssessment(@RequestParam("assessmentId") Long assessmentId,
                                                                             @RequestParam("babyId") Long babyId) {
        return success(assessmentService.participateAssessment(assessmentId, babyId));
    }

    @PostMapping("/submit-questionnaire")
    @Operation(summary = "提交单个问卷结果（回调）")
    public CommonResult<Boolean> submitQuestionnaireResult(@Valid @RequestBody AppQuestionnaireSubmitReqVO submitReqVO) {
        assessmentService.submitQuestionnaireResult(submitReqVO);
        return success(true);
    }

    @PostMapping("/submit")
    @Operation(summary = "提交测评结果")
    public CommonResult<Boolean> submitAssessment(@Valid @RequestBody AppAssessmentSubmitReqVO submitReqVO) {
        assessmentService.submitAssessment(submitReqVO);
        return success(true);
    }

    @GetMapping("/baby-assessments")
    @Operation(summary = "获得宝宝的测评列表")
    public CommonResult<PageResult<AppAssessmentRespVO>> getBabyAssessmentList(@Valid AppAssessmentPageReqVO pageReqVO) {
        return success(assessmentService.getBabyAssessmentPage(pageReqVO));
    }

    @GetMapping("/result")
    @Operation(summary = "查看测评结果")
    @Parameter(name = "id", description = "测评编号", required = true, example = "1024")
    @Parameter(name = "babyId", description = "宝宝编号", required = true, example = "1024")
    public CommonResult<AppAssessmentResultRespVO> getAssessmentResult(@RequestParam("id") Long id, @RequestParam("babyId") Long babyId) {
        return success(assessmentService.getAssessmentResult(id, babyId));
    }
} 

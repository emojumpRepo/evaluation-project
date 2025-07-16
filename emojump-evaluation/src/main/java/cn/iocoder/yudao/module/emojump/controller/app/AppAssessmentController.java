package cn.iocoder.yudao.module.emojump.controller.app;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.app.vo.assessment.*;
import cn.iocoder.yudao.module.emojump.service.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    public CommonResult<AppAssessmentRespVO> getAssessment(@RequestParam("id") Long id) {
        return success(assessmentService.getAppAssessment(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得测评列表")
    public CommonResult<PageResult<AppAssessmentRespVO>> getAssessmentList(@Valid AppAssessmentPageReqVO pageVO) {
        return success(assessmentService.getAppAssessmentPage(pageVO));
    }

    @GetMapping("/published")
    @Operation(summary = "获得已发布测评列表")
    public CommonResult<PageResult<AppAssessmentRespVO>> getPublishedAssessmentList(@Valid AppAssessmentPageReqVO pageVO) {
        return success(assessmentService.getPublishedAssessmentPage(pageVO));
    }

    @PostMapping("/participate")
    @Operation(summary = "参与测评")
    @Parameter(name = "id", description = "测评编号", required = true)
    public CommonResult<AppAssessmentParticipateRespVO> participateAssessment(@RequestParam("id") Long id) {
        return success(assessmentService.participateAssessment(id));
    }

    @PostMapping("/submit")
    @Operation(summary = "提交测评结果")
    public CommonResult<Boolean> submitAssessment(@Valid @RequestBody AppAssessmentSubmitReqVO submitReqVO) {
        assessmentService.submitAssessment(submitReqVO);
        return success(true);
    }

    @GetMapping("/my-assessments")
    @Operation(summary = "获得我的测评列表")
    public CommonResult<PageResult<AppAssessmentRespVO>> getMyAssessmentList(@Valid AppAssessmentPageReqVO pageVO) {
        return success(assessmentService.getMyAssessmentPage(pageVO));
    }

    @GetMapping("/result")
    @Operation(summary = "获得测评结果")
    @Parameter(name = "id", description = "测评编号", required = true, example = "1024")
    public CommonResult<AppAssessmentResultRespVO> getAssessmentResult(@RequestParam("id") Long id) {
        return success(assessmentService.getAssessmentResult(id));
    }
} 

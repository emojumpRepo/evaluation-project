package cn.iocoder.yudao.module.emojump.controller.admin.assessment;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.service.assessment.AssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 测评管理")
@RestController
@RequestMapping("/emojump/assessment")
@Validated
public class AssessmentController {

    @Resource
    private AssessmentService assessmentService;

    public AssessmentController() {
        System.out.println(getClass() + "生效啦！！！");
    }

    @PostMapping("/create")
    @Operation(summary = "创建测评")
    @PreAuthorize("@ss.hasPermission('emojump:assessment:create')")
    public CommonResult<Long> createAssessment(@Valid @RequestBody AssessmentCreateReqVO createReqVO) {
        return success(assessmentService.createAssessment(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新测评")
    @PreAuthorize("@ss.hasPermission('emojump:assessment:update')")
    public CommonResult<Boolean> updateAssessment(@Valid @RequestBody AssessmentUpdateReqVO updateReqVO) {
        assessmentService.updateAssessment(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除测评")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:assessment:delete')")
    public CommonResult<Boolean> deleteAssessment(@RequestParam("id") Long id) {
        assessmentService.deleteAssessment(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得测评")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emojump:assessment:query')")
    public CommonResult<AssessmentRespVO> getAssessment(@RequestParam("id") Long id) {
        return success(assessmentService.getAssessment(id));
    }

    @GetMapping("/list")
    @Operation(summary = "获得测评列表")
    @PreAuthorize("@ss.hasPermission('emojump:assessment:query')")
    public CommonResult<PageResult<AssessmentRespVO>> getAssessmentList(@Valid AssessmentPageReqVO pageVO) {
        return success(assessmentService.getAssessmentPage(pageVO));
    }

    @PostMapping("/publish")
    @Operation(summary = "发布测评")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:assessment:publish')")
    public CommonResult<Boolean> publishAssessment(@RequestParam("id") Long id) {
        assessmentService.publishAssessment(id);
        return success(true);
    }

    @PostMapping("/unpublish")
    @Operation(summary = "取消发布测评")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('emojump:assessment:publish')")
    public CommonResult<Boolean> unpublishAssessment(@RequestParam("id") Long id) {
        assessmentService.unpublishAssessment(id);
        return success(true);
    }

    @GetMapping("/available-questionnaires")
    @Operation(summary = "获取可选择的问卷列表")
    @PreAuthorize("@ss.hasPermission('emojump:assessment:query')")
    public CommonResult<PageResult<QuestionnaireRespVO>> getAvailableQuestionnaires(@Valid QuestionnairePageReqVO pageVO) {
        return success(assessmentService.getAvailableQuestionnaires(pageVO));
    }
} 

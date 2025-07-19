package cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.service.assessmentresult.AssessmentResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 测评结果")
@RestController
@RequestMapping("/emojump/assessment-result")
@Validated
public class AssessmentResultController {

    @Resource
    private AssessmentResultService assessmentResultService;

    @GetMapping("/get")
    @Operation(summary = "获得测评结果")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('emojump:assessment-result:query')")
    public CommonResult<AssessmentResultRespVO> getAssessmentResult(@RequestParam("id") Long id) {
        AssessmentResultRespVO assessmentResult = assessmentResultService.getAssessmentResult(id);
        return success(assessmentResult);
    }

    @GetMapping("/page")
    @Operation(summary = "获得测评结果分页")
    @PreAuthorize("@ss.hasPermission('emojump:assessment-result:query')")
    public CommonResult<PageResult<AssessmentResultRespVO>> getAssessmentResultPage(@Valid AssessmentResultPageReqVO pageVO) {
        PageResult<AssessmentResultRespVO> pageResult = assessmentResultService.getAssessmentResultPage(pageVO);
        return success(pageResult);
    }

}

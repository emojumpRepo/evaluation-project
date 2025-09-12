package cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.GenerateAssessmentResultReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.GenerateAssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.service.assessmentresult.AssessmentResultService;
import cn.iocoder.yudao.module.emojump.service.assessmentresult.AssessmentResultPdfGenerator;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.servlet.ServletUtils.writeAttachment;

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

    @PostMapping("/generate-result")
    @Operation(summary = "生成测评结果")
    @PermitAll
    public CommonResult<GenerateAssessmentResultRespVO> generateAssessmentResult(@Valid @RequestBody GenerateAssessmentResultReqVO generateReqVO) {
        GenerateAssessmentResultRespVO respVO = new GenerateAssessmentResultRespVO();
        respVO.setAssessmentId(generateReqVO.getAssessmentId());
        respVO.setBabyId(generateReqVO.getBabyId());

        try {
            // 调用服务生成测评结果
            Long assessmentResultId = assessmentResultService.generateAssessmentResult(
                generateReqVO.getAssessmentId(), generateReqVO.getBabyId());

            respVO.setAssessmentResultId(assessmentResultId);
            respVO.setSuccess(true);

            return success(respVO);

        } catch (Exception e) {
            respVO.setSuccess(false);
            respVO.setErrorMessage(e.getMessage());
            return success(respVO);
        }
    }



    @GetMapping("/export-result-pdf")
    @Operation(summary = "导出测评结果 PDF")
    @PreAuthorize("@ss.hasPermission('emojump:assessment-result:export')")
    public void exportAssessmentResultPdf(@RequestParam("assessmentResultId") Long assessmentResultId,
                                          HttpServletResponse response) throws IOException {
        AssessmentResultRespVO result = assessmentResultService.getAssessmentResult(assessmentResultId);
        byte[] pdfBytes = AssessmentResultPdfGenerator.generate(result);
        String fileName = String.format("测评结果-%s.pdf", assessmentResultId);
        writeAttachment(response, fileName, pdfBytes);
    }

}

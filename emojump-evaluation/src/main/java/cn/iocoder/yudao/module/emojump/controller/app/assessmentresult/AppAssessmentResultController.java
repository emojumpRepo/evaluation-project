package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.GenerateAssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.UserAssessmentRecordsReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.UserAssessmentRecordsRespVO;
import cn.iocoder.yudao.module.emojump.service.assessmentresult.AssessmentResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 测评结果")
@RestController
@RequestMapping("/emojump/assessment-result")
@Slf4j
public class AppAssessmentResultController {

    @Resource
    private AssessmentResultService assessmentResultService;

    @GetMapping("/generate-result")
    @Operation(summary = "生成测评结果")
    @PermitAll
    public CommonResult<GenerateAssessmentResultRespVO> generateAssessmentResult(@RequestParam("assessmentId") Long assessmentId, @RequestParam("babyId") Long babyId) {
        GenerateAssessmentResultRespVO respVO = new GenerateAssessmentResultRespVO();
        respVO.setAssessmentId(assessmentId);
        respVO.setBabyId(babyId);

        try {
            // 调用服务生成测评结果
            Long assessmentResultId = assessmentResultService.generateAssessmentResult(assessmentId, babyId);

            respVO.setAssessmentResultId(assessmentResultId);
            respVO.setSuccess(true);

            return success(respVO);

        } catch (Exception e) {
            respVO.setSuccess(false);
            respVO.setErrorMessage(e.getMessage());
            return success(respVO);
        }
    }

    @GetMapping("/history-results")
    @Operation(summary = "查询历史测评结果")
    @PermitAll
    public CommonResult<java.util.List<AssessmentResultRespVO>> getHistoryAssessmentResults(@RequestParam("assessmentId") Long assessmentId, @RequestParam("babyId") Long babyId) {
        try {
            // 调用服务查询历史测评结果
            java.util.List<AssessmentResultRespVO> historyResults = assessmentResultService.getHistoryAssessmentResults(assessmentId, babyId);

            return success(historyResults);

        } catch (Exception e) {
            log.error("[getHistoryAssessmentResults] 查询历史测评结果失败，测评ID: {}, 宝宝ID: {}, 错误: {}", 
                assessmentId, babyId, e.getMessage(), e);
            return success(java.util.Collections.emptyList());
        }
    }

    @GetMapping("/latest-result")
    @Operation(summary = "获取最新测评结果")
    @PermitAll
    public CommonResult<AssessmentResultRespVO> getLatestAssessmentResult(@RequestParam("assessmentId") Long assessmentId, @RequestParam("babyId") Long babyId) {
        try {
            // 调用服务获取最新测评结果
            AssessmentResultRespVO latestResult = assessmentResultService.getLatestAssessmentResult(
                assessmentId, babyId);

            return success(latestResult);

        } catch (Exception e) {
            log.error("[getLatestAssessmentResult] 获取最新测评结果失败，测评ID: {}, 宝宝ID: {}, 错误: {}", 
                assessmentId, babyId, e.getMessage(), e);
            
            // 返回null表示没有找到记录
            return success(null);
        }
    }

    @PostMapping("/user-assessment-records")
    @Operation(summary = "查询用户测评记录")
    public CommonResult<UserAssessmentRecordsRespVO> getUserAssessmentRecords(@Valid @RequestBody UserAssessmentRecordsReqVO reqVO) {
        try {
            // 调用服务查询用户测评记录
            UserAssessmentRecordsRespVO respVO = assessmentResultService.getUserAssessmentRecords(reqVO.getUserId());
            
            return success(respVO);
            
        } catch (Exception e) {
            log.error("[getUserAssessmentRecords] 查询用户测评记录失败，用户ID: {}, 错误: {}", 
                reqVO.getUserId(), e.getMessage(), e);
            // 返回空列表而不是抛出异常
            UserAssessmentRecordsRespVO emptyResp = new UserAssessmentRecordsRespVO();
            emptyResp.setAssessmentIds(java.util.Collections.emptyList());
            return success(emptyResp);
        }
    }

}

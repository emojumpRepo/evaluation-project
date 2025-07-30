package cn.iocoder.yudao.module.emojump.controller.app.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.GenerateAssessmentResultReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.GenerateAssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.HistoryAssessmentResultReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.HistoryAssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.UserAssessmentRecordsReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.UserAssessmentRecordsRespVO;
import cn.iocoder.yudao.module.emojump.service.assessmentresult.AssessmentResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 测评结果")
@RestController
@RequestMapping("/emojump/assessment-result")
@Validated
@Slf4j
public class AppAssessmentResultController {

    @Resource
    private AssessmentResultService assessmentResultService;

    @PostMapping("/generate-result")
    @Operation(summary = "生成测评结果")
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

    @PostMapping("/history-results")
    @Operation(summary = "查询历史测评结果")
    public CommonResult<java.util.List<HistoryAssessmentResultRespVO>> getHistoryAssessmentResults(@Valid @RequestBody HistoryAssessmentResultReqVO reqVO) {
        try {
            // 调用服务查询历史测评结果
            java.util.List<HistoryAssessmentResultRespVO> historyResults = assessmentResultService.getHistoryAssessmentResults(
                reqVO.getAssessmentId(), reqVO.getBabyId());

            return success(historyResults);

        } catch (Exception e) {
            log.error("[getHistoryAssessmentResults] 查询历史测评结果失败，测评ID: {}, 宝宝ID: {}, 错误: {}", 
                reqVO.getAssessmentId(), reqVO.getBabyId(), e.getMessage(), e);
            return success(java.util.Collections.emptyList());
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

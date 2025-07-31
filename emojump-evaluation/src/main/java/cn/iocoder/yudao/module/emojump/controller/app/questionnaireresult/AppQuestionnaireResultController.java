package cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.GenerateResultReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.GenerateResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireAnswerSubmitReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppBabyQuestionnaireResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultListRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaireresult.vo.AppQuestionnaireResultVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaireresult.EmoQuestionnaireResultMapper;
import cn.iocoder.yudao.module.emojump.service.questionnaireresult.QuestionnaireResultService;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.questionnaire.QuestionnaireResultGeneratorService;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.questionnaire.QuestionnaireResultDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 问卷结果管理")
@RestController
@RequestMapping("/emojump/questionnaire-result")
@Validated
public class AppQuestionnaireResultController {

    @Resource
    private QuestionnaireResultService questionnaireResultService;

    @Resource
    private QuestionnaireResultGeneratorService resultGeneratorService;

    @Resource
    private EmoQuestionnaireResultMapper emoQuestionnaireResultMapper;

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

    @PostMapping("/submit-answer")
    @Operation(summary = "提交问卷答案")
//    @PreAuthorize("@ss.hasPermission('emojump:questionnaire-result:create')")
    @PermitAll
    public CommonResult<Long> submitQuestionnaireAnswer(@Valid @RequestBody QuestionnaireAnswerSubmitReqVO submitReqVO) {
        return success(questionnaireResultService.submitQuestionnaireAnswer(submitReqVO));
    }

    @PostMapping("/generate-result")
    @Operation(summary = "生成问卷结果")
    @PermitAll
    public CommonResult<GenerateResultRespVO> generateQuestionnaireResult(@Valid @RequestBody GenerateResultReqVO generateReqVO) {

        try {
            // 1. 检查是否支持该问卷
            if (!resultGeneratorService.isSupported(generateReqVO.getQuestionnaireId())) {
                return success(GenerateResultRespVO.unsupported(generateReqVO.getQuestionnaireId()));
            }

            // 2. 根据测评结果ID查询记录
            EmoQuestionnaireResultDO resultRecord = questionnaireResultService.getQuestionnaireResult(generateReqVO.getQuestionnaireResultId());
            if (resultRecord == null) {
                return success(GenerateResultRespVO.error(
                        generateReqVO.getQuestionnaireId(),
                        "未找到测评结果记录，测评结果ID: " + generateReqVO.getQuestionnaireResultId()
                ));
            }

            // 3. 验证问卷ID是否匹配
            if (!generateReqVO.getQuestionnaireId().equals(resultRecord.getQuestionnaireId())) {
                return success(GenerateResultRespVO.error(
                        generateReqVO.getQuestionnaireId(),
                        "问卷ID不匹配，请求ID: " + generateReqVO.getQuestionnaireId() +
                                ", 记录中的ID: " + resultRecord.getQuestionnaireId()
                ));
            }

            // 4. 获取答案数据（从数据库记录中获取）
            String answerDataJson = resultRecord.getAnswerData();
            if (answerDataJson == null || answerDataJson.trim().isEmpty()) {
                return success(GenerateResultRespVO.error(
                        generateReqVO.getQuestionnaireId(),
                        "测评结果记录中没有答案数据"
                ));
            }

            // 5. 生成结果
            QuestionnaireResultDTO resultDTO = resultGeneratorService.generateResult(
                    generateReqVO.getQuestionnaireId(),
                    answerDataJson
            );

            // 7. 生成时间
            String generateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // 8. 更新数据库
            resultRecord.setResultData(resultDTO.getResultData());
            resultRecord.setReport(resultDTO.getReport());
            resultRecord.setScore(resultDTO.getScore());
            resultRecord.setLevel(resultDTO.getLevel());

            emoQuestionnaireResultMapper.updateById(resultRecord);

            // 9. 返回成功响应
            return success(GenerateResultRespVO.success(
                    generateReqVO.getQuestionnaireId(),
                    resultDTO.getScore(),
                    resultDTO.getLevel(),
                    resultDTO.getResultData(),
                    resultDTO.getReport(),
                    generateTime
            ));

        } catch (Exception e) {
            // 返回错误响应
            return success(GenerateResultRespVO.error(
                    generateReqVO.getQuestionnaireId(),
                    "生成结果失败: " + e.getMessage()
            ));
        }
    }
}
package cn.iocoder.yudao.module.emojump.controller.admin.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.service.questionnaire.QuestionnaireSyncService;
import cn.iocoder.yudao.module.emojump.framework.survey.client.SurveySystemClient;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 问卷同步控制器
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 问卷同步")
@RestController
@RequestMapping("/emojump/questionnaire-sync")
@Validated
@Slf4j
public class QuestionnaireSyncController {

    @Resource
    private QuestionnaireSyncService questionnaireSyncService;

    @Resource
    private SurveySystemClient surveySystemClient;

    @PostMapping("/manual-sync")
    @Operation(summary = "手动同步外部问卷系统数据")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:sync')")
    public CommonResult<QuestionnaireSyncService.QuestionnaireSyncResult> manualSync() {
        log.info("[manualSync] 管理员手动触发问卷同步");
        
        QuestionnaireSyncService.QuestionnaireSyncResult result = questionnaireSyncService.manualSync();
        
        if (result.isSuccess()) {
            return success(result);
        } else {
            return CommonResult.error(500, "同步失败: " + result.getErrorMessage());
        }
    }

    @PostMapping("/test-publish")
    @Operation(summary = "测试发布问卷")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:sync')")
    public CommonResult<String> testPublishSurvey(@RequestParam("surveyId") String surveyId) {
        log.info("[testPublishSurvey] 测试发布问卷，surveyId: {}", surveyId);

        try {
            ExternalServiceResult result = surveySystemClient.publishSurvey(surveyId);

            if (result.isSuccess()) {
                log.info("[testPublishSurvey] 问卷发布成功");
                return success("问卷发布成功");
            } else {
                log.error("[testPublishSurvey] 问卷发布失败: {}", result.getErrorMessage());
                return CommonResult.error(result.getErrorCode() != null ? result.getErrorCode() : 500,
                        result.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("[testPublishSurvey] 测试发布问卷失败", e);
            return CommonResult.error(500, "测试失败: " + e.getMessage());
        }
    }

    @PostMapping("/test-pause")
    @Operation(summary = "测试暂停问卷")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:sync')")
    public CommonResult<String> testPauseSurvey(@RequestParam("surveyId") String surveyId) {
        log.info("[testPauseSurvey] 测试暂停问卷，surveyId: {}", surveyId);

        try {
            ExternalServiceResult result = surveySystemClient.pauseSurvey(surveyId);

            if (result.isSuccess()) {
                log.info("[testPauseSurvey] 问卷暂停成功");
                return success("问卷暂停成功");
            } else {
                log.error("[testPauseSurvey] 问卷暂停失败: {}", result.getErrorMessage());
                return CommonResult.error(result.getErrorCode() != null ? result.getErrorCode() : 500,
                        result.getErrorMessage());
            }

        } catch (Exception e) {
            log.error("[testPauseSurvey] 测试暂停问卷失败", e);
            return CommonResult.error(500, "测试失败: " + e.getMessage());
        }
    }

}

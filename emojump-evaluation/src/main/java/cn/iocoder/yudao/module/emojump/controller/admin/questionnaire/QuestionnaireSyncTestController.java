package cn.iocoder.yudao.module.emojump.controller.admin.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.framework.config.SurveySystemProperties;
import cn.iocoder.yudao.module.emojump.framework.survey.client.SurveySystemClient;
import cn.iocoder.yudao.module.emojump.framework.survey.util.SurveyDataConverter;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalSurveyRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 问卷同步测试控制器
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 问卷同步测试")
@RestController
@RequestMapping("/emojump/questionnaire-sync-test")
@Validated
@Slf4j
public class QuestionnaireSyncTestController {

    @Resource
    private SurveySystemClient surveySystemClient;

    @Resource
    private SurveySystemProperties surveySystemProperties;

    @GetMapping("/test-external-api")
    @Operation(summary = "测试外部问卷系统API")
    @PreAuthorize("@ss.hasPermission('emojump:questionnaire:sync')")
    public CommonResult<List<ExternalSurveyRespVO>> testExternalApi() {
        log.info("[testExternalApi] 测试外部问卷系统API");
        
        try {
            List<ExternalSurveyRespVO> surveys = surveySystemClient.getSurveyListWithRetry();
            
            log.info("[testExternalApi] 外部API返回数据数量: {}", surveys.size());
            for (ExternalSurveyRespVO survey : surveys) {
                log.info("[testExternalApi] 原始数据: ID={}, Title={}, Type={}, Status={}, Path={}, SubmitCount={}",
                        survey.getSurveyMetaId(), survey.getTitle(), survey.getSurveyType(),
                        survey.getCurrentStatus(), survey.getSurveyPath(), survey.getSubmitCount());

                // 显示转换后的数据
                log.info("[testExternalApi] 转换后数据: Link={}, LocalType={}, LocalStatus={}, CompletionCount={}, Description={}",
                        SurveyDataConverter.generateSurveyLink(survey, surveySystemProperties.getBaseUrl()),
                        SurveyDataConverter.convertSurveyType(survey.getSurveyType()),
                        SurveyDataConverter.convertStatus(survey),
                        survey.getSubmitCount() != null ? survey.getSubmitCount() : 0,
                        SurveyDataConverter.generateDescription(survey));
            }
            
            return success(surveys);
        } catch (Exception e) {
            log.error("[testExternalApi] 测试外部API失败", e);
            return CommonResult.error(500, "测试失败: " + e.getMessage());
        }
    }

}

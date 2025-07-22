package cn.iocoder.yudao.module.emojump.controller.admin.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.service.questionnaire.QuestionnaireSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

}

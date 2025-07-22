package cn.iocoder.yudao.module.emojump.job;

import cn.iocoder.yudao.framework.quartz.core.handler.JobHandler;
import cn.iocoder.yudao.module.emojump.service.questionnaire.QuestionnaireSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 问卷同步定时任务
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class QuestionnaireSyncJob implements JobHandler {

    @Resource
    private QuestionnaireSyncService questionnaireSyncService;

    @Override
    public String execute(String param) throws Exception {
        log.info("[execute] 开始执行问卷同步定时任务");
        
        try {
            QuestionnaireSyncService.QuestionnaireSyncResult result = questionnaireSyncService.syncQuestionnaires();
            
            String resultMessage = String.format(
                "问卷同步完成 - 总处理: %d, 新增: %d, 更新: %d, 失效: %d",
                result.getTotalProcessed(),
                result.getNewAdded(),
                result.getUpdated(),
                result.getInvalidated()
            );
            
            if (result.isSuccess()) {
                log.info("[execute] {}", resultMessage);
                return resultMessage;
            } else {
                log.error("[execute] 问卷同步失败: {}", result.getErrorMessage());
                return "问卷同步失败: " + result.getErrorMessage();
            }
            
        } catch (Exception e) {
            log.error("[execute] 问卷同步定时任务执行异常", e);
            throw e;
        }
    }

}

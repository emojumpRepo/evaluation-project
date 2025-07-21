package cn.iocoder.yudao.module.emojump.service.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireAccessRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.convert.questionnaire.QuestionnaireConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireAccessDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireAccessMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.enums.QuestionnaireStatusEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;
import static cn.iocoder.yudao.module.emojump.enums.ErrorCodeConstants.*;

/**
 * 问卷管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Resource
    private QuestionnaireMapper questionnaireMapper;

    @Resource
    private QuestionnaireAccessMapper questionnaireAccessMapper;

    @Override
    public Long createQuestionnaire(@Valid QuestionnaireCreateReqVO createReqVO) {
        // 插入
        QuestionnaireDO questionnaire = QuestionnaireConvert.INSTANCE.convert(createReqVO);
        questionnaire.setStatus(QuestionnaireStatusEnum.DRAFT.getStatus());
        questionnaire.setAccessCount(0);
        questionnaire.setCompletionCount(0);
        questionnaire.setIsOpen(createReqVO.getIsOpen() != null ? createReqVO.getIsOpen() : true);
        questionnaireMapper.insert(questionnaire);
        // 返回
        return questionnaire.getId();
    }

    @Override
    public void updateQuestionnaire(@Valid QuestionnaireUpdateReqVO updateReqVO) {
        // 校验存在
        validateQuestionnaireExists(updateReqVO.getId());
        // 更新
        QuestionnaireDO updateObj = QuestionnaireConvert.INSTANCE.convert(updateReqVO);
        questionnaireMapper.updateById(updateObj);
    }

    @Override
    public void deleteQuestionnaire(Long id) {
        // 校验存在
        validateQuestionnaireExists(id);
        // 删除
        questionnaireMapper.deleteById(id);
    }

    private void validateQuestionnaireExists(Long id) {
        if (questionnaireMapper.selectById(id) == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }
    }

    @Override
    public QuestionnaireRespVO getQuestionnaire(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        return QuestionnaireConvert.INSTANCE.convert(questionnaire);
    }

    @Override
    public PageResult<QuestionnaireRespVO> getQuestionnairePage(QuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    @Transactional
    public void publishQuestionnaire(Long id) {
        // 校验存在
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 更新状态为已发布
        QuestionnaireDO updateObj = new QuestionnaireDO();
        updateObj.setId(id);
        updateObj.setStatus(QuestionnaireStatusEnum.PUBLISHED.getStatus());
        questionnaireMapper.updateById(updateObj);
    }

    @Override
    @Transactional
    public void unpublishQuestionnaire(Long id) {
        // 校验存在
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 更新状态为已下线
        QuestionnaireDO updateObj = new QuestionnaireDO();
        updateObj.setId(id);
        updateObj.setStatus(QuestionnaireStatusEnum.OFFLINE.getStatus());
        questionnaireMapper.updateById(updateObj);
    }

    @Override
    public PageResult<QuestionnaireRespVO> getPublishedQuestionnairePage(QuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPublishedPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    public Boolean testQuestionnaireLink(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 这里可以添加实际的链接测试逻辑
        // 比如发送HTTP请求测试链接是否可访问
        // 暂时简单返回true
        return true;
    }

    // ==================== App端接口实现 ====================

    @Override
    public AppQuestionnaireRespVO getAppQuestionnaire(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }
        return QuestionnaireConvert.INSTANCE.convertApp(questionnaire);
    }

    @Override
    public PageResult<AppQuestionnaireRespVO> getPublishedAppQuestionnairePage(AppQuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPublishedPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertAppPage(pageResult);
    }

    @Override
    public AppQuestionnaireAccessRespVO getQuestionnaireAccess(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 创建访问响应
        AppQuestionnaireAccessRespVO accessRespVO = new AppQuestionnaireAccessRespVO();
        accessRespVO.setId(id);
        accessRespVO.setLink(questionnaire.getLink());
        accessRespVO.setAccessToken(generateAccessToken());
        accessRespVO.setExpireTime(LocalDateTime.now().plusHours(24)); // 24小时后过期

        return accessRespVO;
    }

    @Override
    @Transactional
    public void recordQuestionnaireAccess(Long id) {
        QuestionnaireDO questionnaire = questionnaireMapper.selectById(id);
        if (questionnaire == null) {
            throw exception(QUESTIONNAIRE_NOT_EXISTS);
        }

        // 增加访问次数
        questionnaireMapper.updateById(new QuestionnaireDO().setId(id).setAccessCount(questionnaire.getAccessCount() + 1));

        // 记录访问日志
        QuestionnaireAccessDO accessLog = new QuestionnaireAccessDO();
        accessLog.setQuestionnaireId(id);
        accessLog.setUserId(getLoginUserId());
        accessLog.setIpAddress(ServletUtils.getClientIP());
        accessLog.setUserAgent(ServletUtils.getUserAgent());
        accessLog.setAccessTime(LocalDateTime.now());
        questionnaireAccessMapper.insert(accessLog);
    }

    @Override
    public PageResult<AppQuestionnaireRespVO> getPopularQuestionnairePage(AppQuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.selectPopularPage(pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertAppPage(pageResult);
    }

    @Override
    public PageResult<AppQuestionnaireRespVO> searchQuestionnaire(String keyword, AppQuestionnairePageReqVO pageReqVO) {
        PageResult<QuestionnaireDO> pageResult = questionnaireMapper.searchQuestionnaire(keyword, pageReqVO);
        return QuestionnaireConvert.INSTANCE.convertAppPage(pageResult);
    }

    private String generateAccessToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
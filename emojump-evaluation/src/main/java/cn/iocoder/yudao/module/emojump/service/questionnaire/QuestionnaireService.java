package cn.iocoder.yudao.module.emojump.service.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaire.QuestionnaireUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireAccessRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnairePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.questionnaire.vo.AppQuestionnaireRespVO;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalServiceResult;

import javax.validation.Valid;

/**
 * 问卷管理 Service 接口
 */
public interface QuestionnaireService {

    /**
     * 创建问卷
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createQuestionnaire(@Valid QuestionnaireCreateReqVO createReqVO);

    /**
     * 更新问卷
     *
     * @param updateReqVO 更新信息
     */
    void updateQuestionnaire(@Valid QuestionnaireUpdateReqVO updateReqVO);

    /**
     * 删除问卷
     *
     * @param id 编号
     */
    void deleteQuestionnaire(Long id);

    /**
     * 获得问卷
     *
     * @param id 编号
     * @return 问卷
     */
    QuestionnaireRespVO getQuestionnaire(Long id);

    /**
     * 获得问卷分页
     *
     * @param pageReqVO 分页查询
     * @return 问卷分页
     */
    PageResult<QuestionnaireRespVO> getQuestionnairePage(QuestionnairePageReqVO pageReqVO);

    /**
     * 发布问卷
     *
     * @param id 编号
     */
    void publishQuestionnaire(Long id);

    /**
     * 下线问卷
     *
     * @param id 编号
     */
    void unpublishQuestionnaire(Long id);

    /**
     * 获得已发布问卷分页
     *
     * @param pageReqVO 分页查询
     * @return 问卷分页
     */
    PageResult<QuestionnaireRespVO> getPublishedQuestionnairePage(QuestionnairePageReqVO pageReqVO);

    /**
     * 测试问卷链接
     *
     * @param id 编号
     * @return 测试结果
     */
    Boolean testQuestionnaireLink(Long id);

    // ==================== App端接口 ====================

    /**
     * 获得问卷信息（App端）
     *
     * @param id 编号
     * @return 问卷信息
     */
    AppQuestionnaireRespVO getAppQuestionnaire(Long id);

    /**
     * 获得已发布问卷分页（App端）
     *
     * @param pageReqVO 分页查询
     * @return 问卷分页
     */
    PageResult<AppQuestionnaireRespVO> getPublishedAppQuestionnairePage(AppQuestionnairePageReqVO pageReqVO);

    /**
     * 获得问卷访问链接
     *
     * @param id 编号
     * @return 访问信息
     */
    AppQuestionnaireAccessRespVO getQuestionnaireAccess(Long id);

    /**
     * 记录问卷访问
     *
     * @param id 编号
     */
    void recordQuestionnaireAccess(Long id);

    /**
     * 获得热门问卷分页
     *
     * @param pageReqVO 分页查询
     * @return 问卷分页
     */
    PageResult<AppQuestionnaireRespVO> getPopularQuestionnairePage(AppQuestionnairePageReqVO pageReqVO);

    /**
     * 搜索问卷
     *
     * @param keyword 关键字
     * @param pageReqVO 分页查询
     * @return 问卷分页
     */
    PageResult<AppQuestionnaireRespVO> searchQuestionnaire(String keyword, AppQuestionnairePageReqVO pageReqVO);

    /**
     * 发布问卷到外部系统
     *
     * @param id 问卷ID
     * @return 操作结果
     */
    ExternalServiceResult publishQuestionnaireToExternal(Long id);

    /**
     * 暂停外部系统的问卷
     *
     * @param id 问卷ID
     * @return 操作结果
     */
    ExternalServiceResult pauseQuestionnaireInExternal(Long id);
}

package cn.iocoder.yudao.module.emojump.service.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.QuestionnaireResultRespVO;
import cn.iocoder.yudao.module.emojump.convert.assessment.AssessmentResultConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentResultDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire.QuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentResultMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireResultMapper;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emojump.enums.ErrorCodeConstants.ASSESSMENT_NOT_EXISTS;

/**
 * 测评结果 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AssessmentResultServiceImpl implements AssessmentResultService {

    @Resource
    private AssessmentResultMapper assessmentResultMapper;
    @Resource
    private QuestionnaireResultMapper questionnaireResultMapper;
    @Resource
    private AssessmentMapper assessmentMapper;
    @Resource
    private QuestionnaireMapper questionnaireMapper;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private MemberBabyService memberBabyService;

    @Override
    public PageResult<AssessmentResultRespVO> getAssessmentResultPage(@Valid AssessmentResultPageReqVO pageReqVO) {
        PageResult<AssessmentResultDO> pageResult = assessmentResultMapper.selectPage(pageReqVO);
        if (org.springframework.util.CollectionUtils.isEmpty(pageResult.getList())) {
            return PageResult.empty();
        }
        // 拼接数据
        List<AssessmentResultRespVO> respList = AssessmentResultConvert.INSTANCE.convertList(pageResult.getList());
        // 1. 拼接测评信息
        List<Long> assessmentIds = CollectionUtils.convertList(respList, AssessmentResultRespVO::getAssessmentId);
        Map<Long, AssessmentDO> assessmentMap = assessmentMapper.selectBatchIds(assessmentIds).stream().collect(Collectors.toMap(AssessmentDO::getId, a -> a));
        // 2. 拼接宝宝信息
        List<Long> babyIds = CollectionUtils.convertList(respList, AssessmentResultRespVO::getBabyId);
        Map<Long, MemberBabyDO> babyMap = babyIds.stream().distinct().collect(Collectors.toMap(
            id -> id,
            id -> memberBabyService.getBaby(id),
            (a, b) -> a // 合并函数，避免重复key异常
        ));

        respList.forEach(resp -> {
            if (assessmentMap.containsKey(resp.getAssessmentId())) {
                resp.setAssessmentTitle(assessmentMap.get(resp.getAssessmentId()).getTitle());
            }
            if (babyMap.containsKey(resp.getBabyId()) && babyMap.get(resp.getBabyId()) != null) {
                resp.setBabyName(babyMap.get(resp.getBabyId()).getName());
            }
        });
        return new PageResult<>(respList, pageResult.getTotal());
    }

    @Override
    public AssessmentResultRespVO getAssessmentResult(Long id, Long babyId) {
        AssessmentResultDO result = assessmentResultMapper.selectById(id);
        if (result == null) {
            throw exception(ASSESSMENT_NOT_EXISTS); // Or a more specific error
        }
        AssessmentResultRespVO respVO = AssessmentResultConvert.INSTANCE.convert(result);

        // 拼接测评信息
        AssessmentDO assessment = assessmentMapper.selectById(result.getAssessmentId());
        if (assessment != null) {
            respVO.setAssessmentTitle(assessment.getTitle());
        }
        // 拼接宝宝信息
        MemberBabyDO baby = memberBabyService.getBaby(babyId);
        if (baby != null) {
            respVO.setBabyName(baby.getName());
        }

        // 拼接问卷结果信息
        List<QuestionnaireResultDO> questionnaireResults = questionnaireResultMapper.selectList(
                QuestionnaireResultDO::getAssessmentResultId, id);
        if (questionnaireResults != null && !questionnaireResults.isEmpty()) {
            List<Long> questionnaireIds = CollectionUtils.convertList(questionnaireResults, QuestionnaireResultDO::getQuestionnaireId);
            Map<Long, QuestionnaireDO> questionnaireMap = questionnaireMapper.selectBatchIds(questionnaireIds).stream().collect(Collectors.toMap(QuestionnaireDO::getId, q -> q));

            respVO.setQuestionnaireResults(CollectionUtils.convertList(questionnaireResults, qr -> {
                QuestionnaireResultRespVO qrResp = new QuestionnaireResultRespVO();
                qrResp.setId(qr.getId());
                qrResp.setQuestionnaireId(qr.getQuestionnaireId());
                if (questionnaireMap.containsKey(qr.getQuestionnaireId())) {
                    qrResp.setQuestionnaireTitle(questionnaireMap.get(qr.getQuestionnaireId()).getTitle());
                }
                qrResp.setResultData(qr.getResultData());
                qrResp.setScore(qr.getScore());
                qrResp.setLevel(qr.getLevel());
                qrResp.setReport(qr.getReport());
                qrResp.setCompletedTime(qr.getCompletedTime());
                return qrResp;
            }));
        } else {
            respVO.setQuestionnaireResults(Collections.emptyList());
        }

        return respVO;
    }
}
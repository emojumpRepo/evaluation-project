package cn.iocoder.yudao.module.emojump.service.assessmentresult;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.collection.CollectionUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.QuestionnaireResultRespVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessmentresult.vo.UserAssessmentRecordsRespVO;
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
import cn.iocoder.yudao.module.emojump.service.resultgenerator.assessment.AssessmentResultGeneratorService;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.dto.assessment.AssessmentResultDTO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaireresult.EmoQuestionnaireResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
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
@Slf4j
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
    @Resource
    private AssessmentResultGeneratorService assessmentResultGeneratorService;
    @Resource
    private EmoQuestionnaireResultMapper emoQuestionnaireResultMapper;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateAssessmentResult(Long assessmentId, Long babyId) {
        log.info("[generateAssessmentResult] 开始生成测评结果，测评ID: {}, 宝宝ID: {}", assessmentId, babyId);

        try {
            // 1. 查找未完成的测评结果记录
            AssessmentResultDO unfinishedAssessmentResult = assessmentResultMapper.selectOne(
                new LambdaQueryWrapperX<AssessmentResultDO>()
                    .eq(AssessmentResultDO::getAssessmentId, assessmentId)
                    .eq(AssessmentResultDO::getBabyId, babyId)
                    .eq(AssessmentResultDO::getStatus, 0)
                    .orderByDesc(AssessmentResultDO::getId)
                    .last("LIMIT 1")
            );

            if (unfinishedAssessmentResult == null) {
                log.error("[generateAssessmentResult] 未找到未完成的测评结果记录，测评ID: {}, 宝宝ID: {}", assessmentId, babyId);
                throw new RuntimeException("未找到未完成的测评结果记录");
            }

            Long assessmentResultId = unfinishedAssessmentResult.getId();
            log.info("[generateAssessmentResult] 找到未完成的测评结果记录，ID: {}", assessmentResultId);

            // 2. 根据测评结果ID查找对应的问卷结果记录
            List<EmoQuestionnaireResultDO> questionnaireResults = emoQuestionnaireResultMapper.selectList(
                new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                    .eq(EmoQuestionnaireResultDO::getAssessmentResultId, assessmentResultId)
                    .isNotNull(EmoQuestionnaireResultDO::getScore)
                    .orderByDesc(EmoQuestionnaireResultDO::getCompletedTime)
            );

            if (questionnaireResults.isEmpty()) {
                log.error("[generateAssessmentResult] 未找到有效的问卷结果记录，测评结果ID: {}", assessmentResultId);
                throw new RuntimeException("未找到有效的问卷结果记录");
            }

            log.info("[generateAssessmentResult] 找到 {} 个问卷结果记录", questionnaireResults.size());

            // 3. 获取问卷信息
            List<Long> questionnaireIds = questionnaireResults.stream()
                .map(EmoQuestionnaireResultDO::getQuestionnaireId)
                .collect(Collectors.toList());
            Map<Long, QuestionnaireDO> questionnaireMap = questionnaireMapper.selectBatchIds(questionnaireIds)
                .stream()
                .collect(Collectors.toMap(QuestionnaireDO::getId, q -> q));

            // 4. 构建问卷结果项列表
            List<AssessmentResultDTO.QuestionnaireResultItem> questionnaireResultItems = questionnaireResults.stream()
                .map(qr -> {
                    String questionnaireName = questionnaireMap.containsKey(qr.getQuestionnaireId()) 
                        ? questionnaireMap.get(qr.getQuestionnaireId()).getTitle() 
                        : "问卷" + qr.getQuestionnaireId();
                    
                    return AssessmentResultDTO.QuestionnaireResultItem.builder()
                        .questionnaireId(qr.getQuestionnaireId())
                        .questionnaireName(questionnaireName)
                        .score(qr.getScore())
                        .level(qr.getLevel())
                        .resultData(qr.getResultData())
                        .build();
                })
                .collect(Collectors.toList());

            // 5. 生成测评结果
            AssessmentResultDTO assessmentResult = assessmentResultGeneratorService.generateResult(
                assessmentId, babyId, questionnaireResultItems);

            // 6. 更新测评结果记录
            unfinishedAssessmentResult.setOverallScore(assessmentResult.getOverallScore());
            unfinishedAssessmentResult.setOverallLevel(assessmentResult.getOverallLevel());
            unfinishedAssessmentResult.setOverallReport(assessmentResult.getReport());
            unfinishedAssessmentResult.setStatus(1); // 设置为已完成
            unfinishedAssessmentResult.setCompletedTime(LocalDateTime.now());

            assessmentResultMapper.updateById(unfinishedAssessmentResult);

            log.info("[generateAssessmentResult] 测评结果生成成功，ID: {}, 总分: {}, 评级: {}", 
                assessmentResultId, assessmentResult.getOverallScore(), assessmentResult.getOverallLevel());

            return assessmentResultId;

        } catch (Exception e) {
            log.error("[generateAssessmentResult] 生成测评结果失败，测评ID: {}, 宝宝ID: {}, 错误: {}", 
                assessmentId, babyId, e.getMessage(), e);
            throw new RuntimeException("生成测评结果失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AssessmentResultRespVO> getHistoryAssessmentResults(Long assessmentId, Long babyId) {
        log.info("[getHistoryAssessmentResults] 查询历史测评结果，测评ID: {}, 宝宝ID: {}", assessmentId, babyId);

        // 1. 查询该测评和宝宝的所有已完成测评结果
        List<AssessmentResultDO> assessmentResults = assessmentResultMapper.selectList(
            new LambdaQueryWrapperX<AssessmentResultDO>()
                .eq(AssessmentResultDO::getAssessmentId, assessmentId)
                .eq(AssessmentResultDO::getBabyId, babyId)
                .eq(AssessmentResultDO::getStatus, 1) // 只查询已完成的
                .orderByDesc(AssessmentResultDO::getCompletedTime)
        );

        if (assessmentResults.isEmpty()) {
            log.info("[getHistoryAssessmentResults] 未找到历史测评结果，测评ID: {}, 宝宝ID: {}", assessmentId, babyId);
            return Collections.emptyList();
        }

        // 2. 获取测评信息
        AssessmentDO assessment = assessmentMapper.selectById(assessmentId);
        String assessmentTitle = assessment != null ? assessment.getTitle() : "测评" + assessmentId;

        // 3. 获取宝宝信息
        MemberBabyDO baby = memberBabyService.getBaby(babyId);
        String babyName = baby != null ? baby.getName() : "宝宝" + babyId;

        // 4. 获取所有测评结果ID
        List<Long> assessmentResultIds = CollectionUtils.convertList(assessmentResults, AssessmentResultDO::getId);

        // 5. 批量查询问卷结果
        List<EmoQuestionnaireResultDO> allQuestionnaireResults = emoQuestionnaireResultMapper.selectList(
            new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                .in(EmoQuestionnaireResultDO::getAssessmentResultId, assessmentResultIds)
        );

        // 6. 按测评结果ID分组问卷结果
        Map<Long, List<EmoQuestionnaireResultDO>> questionnaireResultMap = allQuestionnaireResults.stream()
            .collect(Collectors.groupingBy(EmoQuestionnaireResultDO::getAssessmentResultId));

        // 7. 获取问卷信息
        List<Long> questionnaireIds = allQuestionnaireResults.stream()
            .map(EmoQuestionnaireResultDO::getQuestionnaireId)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, QuestionnaireDO> questionnaireMap = questionnaireMapper.selectBatchIds(questionnaireIds).stream()
            .collect(Collectors.toMap(QuestionnaireDO::getId, q -> q));

        // 8. 构建返回结果
        List<AssessmentResultRespVO> resultList = 
            assessmentResults.stream().map(result -> {
                AssessmentResultRespVO respVO = new AssessmentResultRespVO();
                
                respVO.setId(result.getId());
                respVO.setAssessmentId(result.getAssessmentId());
                respVO.setAssessmentTitle(assessmentTitle);
                respVO.setBabyId(result.getBabyId());
                respVO.setBabyName(babyName);
                respVO.setOverallScore(result.getOverallScore());
                respVO.setOverallLevel(result.getOverallLevel());
                respVO.setOverallReport(result.getOverallReport());
                respVO.setCompletedTime(result.getCompletedTime());
                respVO.setStatus(result.getStatus());
                respVO.setCreateTime(result.getCreateTime());

                // 设置问卷结果
                List<EmoQuestionnaireResultDO> questionnaireResults = questionnaireResultMap.get(result.getId());
                if (questionnaireResults != null && !questionnaireResults.isEmpty()) {
                    respVO.setQuestionnaireResults(CollectionUtils.convertList(questionnaireResults, qr -> {
                        QuestionnaireResultRespVO qrResp = new QuestionnaireResultRespVO();
                        qrResp.setId(qr.getId());
                        qrResp.setQuestionnaireId(qr.getQuestionnaireId());
                        if (questionnaireMap.containsKey(qr.getQuestionnaireId())) {
                            qrResp.setQuestionnaireTitle(questionnaireMap.get(qr.getQuestionnaireId()).getTitle());
                        }
                        qrResp.setResultData(qr.getResultData());
                        qrResp.setAnswerData(qr.getAnswerData());
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
            }).collect(Collectors.toList());

        log.info("[getHistoryAssessmentResults] 查询到 {} 条历史测评结果", resultList.size());
        return resultList;
    }

    @Override
    public AssessmentResultRespVO getLatestAssessmentResult(Long assessmentId, Long babyId) {
        log.info("[getLatestAssessmentResult] 查询最新测评结果，测评ID: {}, 宝宝ID: {}", assessmentId, babyId);

        // 1. 查询该测评和宝宝的最新已完成测评结果
        AssessmentResultDO latestResult = assessmentResultMapper.selectOne(
            new LambdaQueryWrapperX<AssessmentResultDO>()
                .eq(AssessmentResultDO::getAssessmentId, assessmentId)
                .eq(AssessmentResultDO::getBabyId, babyId)
                .eq(AssessmentResultDO::getStatus, 1) // 只查询已完成的
                .orderByDesc(AssessmentResultDO::getCompletedTime)
                .last("LIMIT 1")
        );

        AssessmentResultRespVO respVO = new AssessmentResultRespVO();

        if (latestResult == null) {
            log.info("[getLatestAssessmentResult] 未找到最新测评结果，测评ID: {}, 宝宝ID: {}", assessmentId, babyId);
            return null;
        }

        // 2. 获取测评信息
        AssessmentDO assessment = assessmentMapper.selectById(assessmentId);
        String assessmentTitle = assessment != null ? assessment.getTitle() : "测评" + assessmentId;

        // 3. 获取宝宝信息
        MemberBabyDO baby = memberBabyService.getBaby(babyId);
        String babyName = baby != null ? baby.getName() : "宝宝" + babyId;

        // 4. 设置基本信息
        respVO.setId(latestResult.getId());
        respVO.setAssessmentId(latestResult.getAssessmentId());
        respVO.setAssessmentTitle(assessmentTitle);
        respVO.setBabyId(latestResult.getBabyId());
        respVO.setBabyName(babyName);
        respVO.setOverallScore(latestResult.getOverallScore());
        respVO.setOverallLevel(latestResult.getOverallLevel());
        respVO.setOverallReport(latestResult.getOverallReport());
        respVO.setCompletedTime(latestResult.getCompletedTime());
        respVO.setCreateTime(latestResult.getCreateTime());

        // 5. 查询问卷结果
        List<EmoQuestionnaireResultDO> questionnaireResults = emoQuestionnaireResultMapper.selectList(
            new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                .eq(EmoQuestionnaireResultDO::getAssessmentResultId, latestResult.getId())
        );

        // 6. 获取问卷信息
        List<Long> questionnaireIds = questionnaireResults.stream()
            .map(EmoQuestionnaireResultDO::getQuestionnaireId)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, QuestionnaireDO> questionnaireMap = questionnaireMapper.selectBatchIds(questionnaireIds).stream()
            .collect(Collectors.toMap(QuestionnaireDO::getId, q -> q));

        // 7. 设置问卷结果
        if (!questionnaireResults.isEmpty()) {
            respVO.setQuestionnaireResults(CollectionUtils.convertList(questionnaireResults, qr -> {
                QuestionnaireResultRespVO qrResp = new QuestionnaireResultRespVO();
                qrResp.setId(qr.getId());
                qrResp.setQuestionnaireId(qr.getQuestionnaireId());
                if (questionnaireMap.containsKey(qr.getQuestionnaireId())) {
                    qrResp.setQuestionnaireTitle(questionnaireMap.get(qr.getQuestionnaireId()).getTitle());
                }
                qrResp.setResultData(qr.getResultData());
                qrResp.setAnswerData(qr.getAnswerData());
                qrResp.setScore(qr.getScore());
                qrResp.setLevel(qr.getLevel());
                qrResp.setReport(qr.getReport());
                qrResp.setCompletedTime(qr.getCompletedTime());
                return qrResp;
            }));
        } else {
            respVO.setQuestionnaireResults(Collections.emptyList());
        }

        log.info("[getLatestAssessmentResult] 成功获取最新测评结果，测评结果ID: {}", latestResult.getId());
        return respVO;
    }

    @Override
    public UserAssessmentRecordsRespVO getUserAssessmentRecords(Long userId) {
        log.info("[getUserAssessmentRecords] 开始查询用户测评记录，用户ID: {}", userId);
        
        // 1. 根据userId查询宝宝ID列表
        List<MemberBabyDO> babyList = memberBabyService.getBabyListByUserId(userId);
        if (babyList == null || babyList.isEmpty()) {
            log.info("[getUserAssessmentRecords] 用户没有宝宝信息，用户ID: {}", userId);
            UserAssessmentRecordsRespVO respVO = new UserAssessmentRecordsRespVO();
            respVO.setAssessmentIds(Collections.emptyList());
            return respVO;
        }
        
        List<Long> babyIds = CollectionUtils.convertList(babyList, MemberBabyDO::getId);
        log.info("[getUserAssessmentRecords] 查询到宝宝ID列表: {}", babyIds);
        
        // 2. 根据babyId列表查询问卷结果数据
        List<EmoQuestionnaireResultDO> questionnaireResults = emoQuestionnaireResultMapper.selectList(
            new LambdaQueryWrapperX<EmoQuestionnaireResultDO>()
                .in(EmoQuestionnaireResultDO::getBabyId, babyIds)
                .isNotNull(EmoQuestionnaireResultDO::getAssessmentId)
        );
        
        log.info("[getUserAssessmentRecords] 查询到问卷结果数据 {} 条", questionnaireResults.size());
        
        // 3. 根据assessmentId去重 (已注释，直接返回原始数据)
        // List<Long> distinctAssessmentIds = questionnaireResults.stream()
        //     .map(EmoQuestionnaireResultDO::getAssessmentId)
        //     .distinct()
        //     .collect(Collectors.toList());
        
        // 直接获取所有assessmentId，不去重
        List<Long> assessmentIds = questionnaireResults.stream()
            .map(EmoQuestionnaireResultDO::getAssessmentId)
            .collect(Collectors.toList());
        
        log.info("[getUserAssessmentRecords] 测评ID列表: {}", assessmentIds);
        
        // 4. 构造响应对象
        UserAssessmentRecordsRespVO respVO = new UserAssessmentRecordsRespVO();
        respVO.setAssessmentIds(assessmentIds);
        
        return respVO;
    }
}
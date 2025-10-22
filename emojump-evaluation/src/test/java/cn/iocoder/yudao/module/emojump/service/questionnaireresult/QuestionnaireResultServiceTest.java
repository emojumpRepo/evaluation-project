package cn.iocoder.yudao.module.emojump.service.questionnaireresult;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.questionnaireresult.vo.QuestionnaireResultUpdateReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult.EmoQuestionnaireResultDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaireresult.EmoQuestionnaireResultMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentQuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.assessment.AssessmentResultMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.questionnaire.QuestionnaireMapper;
import cn.iocoder.yudao.module.emojump.service.resultgenerator.questionnaire.QuestionnaireResultGeneratorService;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static cn.iocoder.yudao.framework.common.util.object.ObjectUtils.cloneIgnoreId;
import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * {@link QuestionnaireResultServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(QuestionnaireResultServiceImpl.class)
public class QuestionnaireResultServiceTest extends BaseDbUnitTest {

    @Resource
    private QuestionnaireResultServiceImpl questionnaireResultService;

    @Resource
    private EmoQuestionnaireResultMapper emoQuestionnaireResultMapper;

    @MockBean
    private MemberBabyService memberBabyService;

    @MockBean
    private AssessmentMapper assessmentMapper;

    @MockBean
    private QuestionnaireMapper questionnaireMapper;

    @MockBean
    private QuestionnaireResultGeneratorService resultGeneratorService;

    @MockBean
    private AssessmentQuestionnaireMapper assessmentQuestionnaireMapper;

    @MockBean
    private AssessmentResultMapper assessmentResultMapper;

    @Test
    public void testCreateQuestionnaireResult_success() {
        // 准备参数
        QuestionnaireResultCreateReqVO reqVO = randomPojo(QuestionnaireResultCreateReqVO.class, o -> {
            // 设置score为有效值，DECIMAL(10,2)最大值为99999999.99
            if (o.getScore() != null) {
                o.setScore(new BigDecimal("50.50"));
            }
        });

        // 调用
        Long questionnaireResultId = questionnaireResultService.createQuestionnaireResult(reqVO);
        // 断言
        assertNotNull(questionnaireResultId);
        // 校验记录的属性是否正确
        EmoQuestionnaireResultDO questionnaireResult = emoQuestionnaireResultMapper.selectById(questionnaireResultId);
        assertPojoEquals(reqVO, questionnaireResult);
    }

    @Test
    public void testUpdateQuestionnaireResult_success() {
        // mock 数据
        EmoQuestionnaireResultDO dbQuestionnaireResult = randomPojo(EmoQuestionnaireResultDO.class, o -> {
            // 设置score为有效值，DECIMAL(10,2)最大值为99999999.99
            if (o.getScore() != null) {
                o.setScore(new BigDecimal("30.00"));
            }
        });
        emoQuestionnaireResultMapper.insert(dbQuestionnaireResult);// @Sql: 先插入出一条存在的数据
        // 准备参数
        QuestionnaireResultUpdateReqVO reqVO = randomPojo(QuestionnaireResultUpdateReqVO.class, o -> {
            o.setId(dbQuestionnaireResult.getId()); // 设置更新的 ID
            // 设置score为有效值
            if (o.getScore() != null) {
                o.setScore(new BigDecimal("60.75"));
            }
        });

        // 调用
        questionnaireResultService.updateQuestionnaireResult(reqVO);
        // 校验是否更新正确
        EmoQuestionnaireResultDO questionnaireResult = emoQuestionnaireResultMapper.selectById(reqVO.getId()); // 获取最新的
        assertPojoEquals(reqVO, questionnaireResult);
    }

    @Test
    public void testDeleteQuestionnaireResult_success() {
        // mock 数据
        EmoQuestionnaireResultDO dbQuestionnaireResult = randomPojo(EmoQuestionnaireResultDO.class, o -> {
            // 设置score为有效值，DECIMAL(10,2)最大值为99999999.99
            if (o.getScore() != null) {
                o.setScore(new BigDecimal("40.25"));
            }
        });
        emoQuestionnaireResultMapper.insert(dbQuestionnaireResult);// @Sql: 先插入出一条存在的数据
        // 准备参数
        Long id = dbQuestionnaireResult.getId();

        // 调用
        questionnaireResultService.deleteQuestionnaireResult(id);
        // 校验数据不存在了
        assertNull(emoQuestionnaireResultMapper.selectById(id));
    }

    @Test
    public void testGetQuestionnaireResultPage() {
        // mock 数据
        EmoQuestionnaireResultDO dbQuestionnaireResult = randomPojo(EmoQuestionnaireResultDO.class, o -> { // 等会查询到
            o.setAssessmentResultId(1L);
            o.setQuestionnaireId(2L);
            o.setLevel("良好");
            o.setCompletedTime(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
            o.setCreateTime(LocalDateTime.of(2023, 1, 1, 0, 0, 0));
            // 设置score为有效值，DECIMAL(10,2)最大值为99999999.99
            if (o.getScore() != null) {
                o.setScore(new BigDecimal("75.50"));
            }
        });
        emoQuestionnaireResultMapper.insert(dbQuestionnaireResult);
        // 测试 assessmentResultId 不匹配
        emoQuestionnaireResultMapper.insert(cloneIgnoreId(dbQuestionnaireResult, o -> o.setAssessmentResultId(2L)));
        // 测试 questionnaireId 不匹配
        emoQuestionnaireResultMapper.insert(cloneIgnoreId(dbQuestionnaireResult, o -> o.setQuestionnaireId(3L)));
        // 测试 level 不匹配
        emoQuestionnaireResultMapper.insert(cloneIgnoreId(dbQuestionnaireResult, o -> o.setLevel("优秀")));
        // 测试 completedTime 不匹配
        emoQuestionnaireResultMapper.insert(cloneIgnoreId(dbQuestionnaireResult, o -> o.setCompletedTime(LocalDateTime.of(2023, 3, 1, 0, 0, 0))));
        // 测试 createTime 不匹配
        emoQuestionnaireResultMapper.insert(cloneIgnoreId(dbQuestionnaireResult, o -> o.setCreateTime(LocalDateTime.of(2023, 3, 1, 0, 0, 0))));
        // 准备参数
        QuestionnaireResultPageReqVO reqVO = new QuestionnaireResultPageReqVO();
        reqVO.setAssessmentResultId(1L);
        reqVO.setQuestionnaireId(2L);
        reqVO.setLevel("良好");
        reqVO.setCompletedTime(new LocalDateTime[]{LocalDateTime.of(2022, 12, 1, 0, 0, 0), LocalDateTime.of(2023, 2, 1, 0, 0, 0)});
        reqVO.setCreateTime(new LocalDateTime[]{LocalDateTime.of(2022, 12, 1, 0, 0, 0), LocalDateTime.of(2023, 2, 1, 0, 0, 0)});

        // 调用
        PageResult<EmoQuestionnaireResultDO> pageResult = questionnaireResultService.getQuestionnaireResultPage(reqVO);
        // 断言
        assertEquals(1, pageResult.getTotal());
        assertEquals(1, pageResult.getList().size());
        // 忽略 creator 字段的比较，因为 BaseDO 会自动填充
        assertPojoEquals(dbQuestionnaireResult, pageResult.getList().get(0), "creator");
    }

    @Test
    public void testGetQuestionnaireResultListByAssessmentResultId() {
        // mock 数据
        EmoQuestionnaireResultDO dbQuestionnaireResult = randomPojo(EmoQuestionnaireResultDO.class, o -> {
            o.setAssessmentResultId(1L);
            // 设置score为有效值，DECIMAL(10,2)最大值为99999999.99
            if (o.getScore() != null) {
                o.setScore(new BigDecimal("85.00"));
            }
        });
        emoQuestionnaireResultMapper.insert(dbQuestionnaireResult);
        // 测试 assessmentResultId 不匹配
        emoQuestionnaireResultMapper.insert(cloneIgnoreId(dbQuestionnaireResult, o -> o.setAssessmentResultId(2L)));

        // 调用
        List<EmoQuestionnaireResultDO> list = questionnaireResultService.getQuestionnaireResultListByAssessmentResultId(1L);
        // 断言
        assertEquals(1, list.size());
        // 忽略 creator 字段的比较，因为 BaseDO 会自动填充
        assertPojoEquals(dbQuestionnaireResult, list.get(0), "creator");
    }

}

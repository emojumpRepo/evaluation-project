package cn.iocoder.yudao.module.member.service.baby;

import cn.iocoder.yudao.framework.test.core.ut.BaseDbUnitTest;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFileCreateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.BabyAssessmentFileDO;
import cn.iocoder.yudao.module.member.dal.mysql.baby.BabyAssessmentFileMapper;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.test.core.util.AssertUtils.assertPojoEquals;
import static cn.iocoder.yudao.framework.test.core.util.RandomUtils.randomPojo;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.BABY_ASSESSMENT_FILE_NOT_EXISTS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link BabyAssessmentFileServiceImpl} 的单元测试类
 *
 * @author 芋道源码
 */
@Import(BabyAssessmentFileServiceImpl.class)
public class BabyAssessmentFileServiceTest extends BaseDbUnitTest {

    @Resource
    private BabyAssessmentFileServiceImpl babyAssessmentFileService;

    @Resource
    private BabyAssessmentFileMapper babyAssessmentFileMapper;

    @Test
    public void testCreateBabyAssessmentFile_success() {
        // 准备参数
        BabyAssessmentFileCreateReqVO createReqVO = randomPojo(BabyAssessmentFileCreateReqVO.class,
            o -> o.setBabyId(1L).setAssessmentId(2L).setFileId(3L));

        // 调用
        Long fileId = babyAssessmentFileService.createBabyAssessmentFile(createReqVO);

        // 断言
        assertNotNull(fileId);
        // 校验记录的属性是否正确
        BabyAssessmentFileDO babyAssessmentFile = babyAssessmentFileMapper.selectById(fileId);
        assertPojoEquals(createReqVO, babyAssessmentFile, "id", "uploadUserId");
        assertNotNull(babyAssessmentFile.getUploadUserId());
    }

    @Test
    public void testUpdateBabyAssessmentFile_success() {
        // mock 数据
        BabyAssessmentFileDO dbBabyAssessmentFile = randomPojo(BabyAssessmentFileDO.class,
            o -> o.setBabyId(1L).setAssessmentId(2L));
        babyAssessmentFileMapper.insert(dbBabyAssessmentFile);

        // 准备参数
        BabyAssessmentFileUpdateReqVO updateReqVO = randomPojo(BabyAssessmentFileUpdateReqVO.class,
            o -> o.setId(dbBabyAssessmentFile.getId()).setDescription("新的描述"));

        // 调用
        babyAssessmentFileService.updateBabyAssessmentFile(updateReqVO);

        // 校验是否更新正确
        BabyAssessmentFileDO babyAssessmentFile = babyAssessmentFileMapper.selectById(updateReqVO.getId());
        assertEquals(updateReqVO.getDescription(), babyAssessmentFile.getDescription());
    }

    @Test
    public void testDeleteBabyAssessmentFile_success() {
        // mock 数据
        BabyAssessmentFileDO dbBabyAssessmentFile = randomPojo(BabyAssessmentFileDO.class,
            o -> o.setBabyId(1L).setAssessmentId(2L));
        babyAssessmentFileMapper.insert(dbBabyAssessmentFile);

        // 调用
        babyAssessmentFileService.deleteBabyAssessmentFile(dbBabyAssessmentFile.getId());

        // 校验数据不存在了
        assertNull(babyAssessmentFileMapper.selectById(dbBabyAssessmentFile.getId()));
    }

    @Test
    public void testGetBabyAssessmentFile() {
        // mock 数据
        BabyAssessmentFileDO dbBabyAssessmentFile = randomPojo(BabyAssessmentFileDO.class,
            o -> o.setBabyId(1L).setAssessmentId(2L));
        babyAssessmentFileMapper.insert(dbBabyAssessmentFile);

        // 调用
        BabyAssessmentFileDO babyAssessmentFile = babyAssessmentFileService.getBabyAssessmentFile(dbBabyAssessmentFile.getId());

        // 校验
        assertPojoEquals(dbBabyAssessmentFile, babyAssessmentFile);
    }

}
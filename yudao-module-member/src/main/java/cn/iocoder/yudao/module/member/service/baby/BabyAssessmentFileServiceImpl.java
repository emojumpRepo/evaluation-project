package cn.iocoder.yudao.module.member.service.baby;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFileCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFilePageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFileUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.BabyAssessmentFileDO;
import cn.iocoder.yudao.module.member.dal.mysql.baby.BabyAssessmentFileMapper;
import cn.iocoder.yudao.module.infra.service.file.FileService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.BABY_ASSESSMENT_FILE_NOT_EXISTS;

/**
 * 宝宝测评附件 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class BabyAssessmentFileServiceImpl implements BabyAssessmentFileService {

    @Resource
    private BabyAssessmentFileMapper babyAssessmentFileMapper;

    @Resource
    private MemberBabyService babyService;

    @Resource
    private FileService fileService;

    @Resource
    private AdminUserApi adminUserApi;

    @Override
    public Long createBabyAssessmentFile(@Valid BabyAssessmentFileCreateReqVO createReqVO) {
        // 校验宝宝存在
        babyService.validateBabyExists(createReqVO.getBabyId());

        // 校验并获取 fileId
        Long fileId = createReqVO.getFileId();
        if (fileId == null && createReqVO.getFileUrl() != null) {
            // 如果 fileId 为空，尝试从 fileUrl 查询
            cn.iocoder.yudao.module.infra.dal.dataobject.file.FileDO file = fileService.getFileByUrl(createReqVO.getFileUrl());
            if (file != null) {
                fileId = file.getId();
            }
        }
        
        if (fileId == null) {
            throw exception(BABY_ASSESSMENT_FILE_NOT_EXISTS);
        }

        // 插入
        BabyAssessmentFileDO babyAssessmentFile = BeanUtils.toBean(createReqVO, BabyAssessmentFileDO.class);
        babyAssessmentFile.setFileId(fileId);
        babyAssessmentFile.setUploadUserId(SecurityFrameworkUtils.getLoginUserId());
        babyAssessmentFileMapper.insert(babyAssessmentFile);

        // 返回
        return babyAssessmentFile.getId();
    }

    @Override
    public void updateBabyAssessmentFile(@Valid BabyAssessmentFileUpdateReqVO updateReqVO) {
        // 校验存在
        validateBabyAssessmentFileExists(updateReqVO.getId());
        // 更新
        BabyAssessmentFileDO updateObj = BeanUtils.toBean(updateReqVO, BabyAssessmentFileDO.class);
        babyAssessmentFileMapper.updateById(updateObj);
    }

    @Override
    public void deleteBabyAssessmentFile(Long id) {
        // 校验存在
        BabyAssessmentFileDO babyAssessmentFile = validateBabyAssessmentFileExists(id);

        // 删除物理文件
        try {
            // FileService 中没有直接根据ID删除文件的方法，这里暂时跳过物理文件删除
            // 在实际使用时可以根据需要实现相应的删除逻辑
            log.warn("[deleteBabyAssessmentFile] 物理文件删除功能待实现，fileId: {}",
                babyAssessmentFile.getFileId());
        } catch (Exception e) {
            log.error("[deleteBabyAssessmentFile] 删除物理文件失败，fileId: {}, error: {}",
                babyAssessmentFile.getFileId(), e.getMessage());
        }

        // 删除数据库记录
        babyAssessmentFileMapper.deleteById(id);
    }

    private BabyAssessmentFileDO validateBabyAssessmentFileExists(Long id) {
        if (id == null) {
            return null;
        }
        BabyAssessmentFileDO babyAssessmentFile = babyAssessmentFileMapper.selectById(id);
        if (babyAssessmentFile == null) {
            throw exception(BABY_ASSESSMENT_FILE_NOT_EXISTS);
        }
        return babyAssessmentFile;
    }

    @Override
    public BabyAssessmentFileDO getBabyAssessmentFile(Long id) {
        return babyAssessmentFileMapper.selectById(id);
    }

    @Override
    public PageResult<BabyAssessmentFileDO> getBabyAssessmentFilePage(BabyAssessmentFilePageReqVO pageReqVO) {
        return babyAssessmentFileMapper.selectPage(
            pageReqVO.getBabyId(),
            pageReqVO.getAssessmentId(),
            pageReqVO.getFileName(),
            pageReqVO
        );
    }

    @Override
    public List<BabyAssessmentFileDO> getBabyAssessmentFileList(Long babyId) {
        return babyAssessmentFileMapper.selectListByBabyId(babyId);
    }

    @Override
    public List<BabyAssessmentFileDO> getAssessmentFileList(Long assessmentId) {
        return babyAssessmentFileMapper.selectListByAssessmentId(assessmentId);
    }

    @Override
    public List<BabyAssessmentFileDO> getBabyAssessmentFileList(Long babyId, Long assessmentId) {
        return babyAssessmentFileMapper.selectListByBabyIdAndAssessmentId(babyId, assessmentId);
    }

    @Override
    public boolean validateFileAccessPermission(Long id, Long userId) {
        BabyAssessmentFileDO file = babyAssessmentFileMapper.selectById(id);
        if (file == null) {
            return false;
        }

        // 验证用户是否是该宝宝的家长
        return babyService.isBabyParent(userId, file.getBabyId());
    }

    @Override
    public BabyAssessmentFileDO getBabyAssessmentFileByFileId(Long fileId) {
        return babyAssessmentFileMapper.selectByFileId(fileId);
    }

    @Override
    public void deleteBabyAssessmentFileWithPhysicalFile(Long id) {
        deleteBabyAssessmentFile(id);
    }

    @Override
    public void deleteBabyAssessmentFileBatch(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }

        List<BabyAssessmentFileDO> fileList = babyAssessmentFileMapper.selectList(BabyAssessmentFileDO::getId, ids);
        for (BabyAssessmentFileDO file : fileList) {
            try {
                // FileService 中没有直接根据ID删除文件的方法，这里暂时跳过物理文件删除
                log.warn("[deleteBabyAssessmentFileBatch] 物理文件删除功能待实现，fileId: {}",
                    file.getFileId());
            } catch (Exception e) {
                log.error("[deleteBabyAssessmentFileBatch] 删除物理文件失败，fileId: {}, error: {}",
                    file.getFileId(), e.getMessage());
            }
        }

        // 批量删除数据库记录
        babyAssessmentFileMapper.deleteBatch(BabyAssessmentFileDO::getId, ids);
    }

}
package cn.iocoder.yudao.module.member.service.baby;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFileCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFilePageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.babyfile.BabyAssessmentFileUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.BabyAssessmentFileDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 宝宝测评附件 Service 接口
 *
 * @author 芋道源码
 */
public interface BabyAssessmentFileService {

    /**
     * 创建宝宝测评附件
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBabyAssessmentFile(@Valid BabyAssessmentFileCreateReqVO createReqVO);

    /**
     * 更新宝宝测评附件
     *
     * @param updateReqVO 更新信息
     */
    void updateBabyAssessmentFile(@Valid BabyAssessmentFileUpdateReqVO updateReqVO);

    /**
     * 删除宝宝测评附件
     *
     * @param id 编号
     */
    void deleteBabyAssessmentFile(Long id);

    /**
     * 获得宝宝测评附件
     *
     * @param id 编号
     * @return 宝宝测评附件
     */
    BabyAssessmentFileDO getBabyAssessmentFile(Long id);

    /**
     * 获得宝宝测评附件分页
     *
     * @param pageReqVO 分页查询
     * @return 宝宝测评附件分页
     */
    PageResult<BabyAssessmentFileDO> getBabyAssessmentFilePage(BabyAssessmentFilePageReqVO pageReqVO);

    /**
     * 获得宝宝的所有附件列表
     *
     * @param babyId 宝宝ID
     * @return 附件列表
     */
    List<BabyAssessmentFileDO> getBabyAssessmentFileList(Long babyId);

    /**
     * 获得测评相关的附件列表
     *
     * @param assessmentId 测评ID
     * @return 附件列表
     */
    List<BabyAssessmentFileDO> getAssessmentFileList(Long assessmentId);

    /**
     * 获得宝宝和测评相关的附件列表
     *
     * @param babyId 宝宝ID
     * @param assessmentId 测评ID
     * @return 附件列表
     */
    List<BabyAssessmentFileDO> getBabyAssessmentFileList(Long babyId, Long assessmentId);

    /**
     * 验证用户是否有权限访问附件
     *
     * @param id 附件ID
     * @param userId 用户ID
     * @return 是否有权限
     */
    boolean validateFileAccessPermission(Long id, Long userId);

    /**
     * 根据文件ID获得附件
     *
     * @param fileId 文件ID
     * @return 宝宝测评附件
     */
    BabyAssessmentFileDO getBabyAssessmentFileByFileId(Long fileId);

    /**
     * 删除附件（包括物理文件）
     *
     * @param id 附件ID
     */
    void deleteBabyAssessmentFileWithPhysicalFile(Long id);

    /**
     * 批量删除附件
     *
     * @param ids 附件ID列表
     */
    void deleteBabyAssessmentFileBatch(List<Long> ids);

}
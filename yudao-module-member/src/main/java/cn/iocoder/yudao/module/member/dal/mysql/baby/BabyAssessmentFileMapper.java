package cn.iocoder.yudao.module.member.dal.mysql.baby;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.BabyAssessmentFileDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 宝宝测评附件 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface BabyAssessmentFileMapper extends BaseMapperX<BabyAssessmentFileDO> {

    default PageResult<BabyAssessmentFileDO> selectPage(Long babyId, Long assessmentId, String fileName, PageParam pageParam) {
        LambdaQueryWrapperX<BabyAssessmentFileDO> wrapper = new LambdaQueryWrapperX<BabyAssessmentFileDO>()
                .eqIfPresent(BabyAssessmentFileDO::getBabyId, babyId)
                .eqIfPresent(BabyAssessmentFileDO::getAssessmentId, assessmentId)
                .likeIfPresent(BabyAssessmentFileDO::getFileName, fileName)
                .orderByDesc(BabyAssessmentFileDO::getCreateTime);
        return selectPage(pageParam, wrapper);
    }

    default List<BabyAssessmentFileDO> selectListByBabyId(Long babyId) {
        return selectList(BabyAssessmentFileDO::getBabyId, babyId);
    }

    default List<BabyAssessmentFileDO> selectListByAssessmentId(Long assessmentId) {
        return selectList(BabyAssessmentFileDO::getAssessmentId, assessmentId);
    }

    default List<BabyAssessmentFileDO> selectListByBabyIdAndAssessmentId(Long babyId, Long assessmentId) {
        LambdaQueryWrapperX<BabyAssessmentFileDO> wrapper = new LambdaQueryWrapperX<BabyAssessmentFileDO>()
                .eq(BabyAssessmentFileDO::getBabyId, babyId)
                .eq(BabyAssessmentFileDO::getAssessmentId, assessmentId)
                .orderByDesc(BabyAssessmentFileDO::getCreateTime);
        return selectList(wrapper);
    }

    default BabyAssessmentFileDO selectByFileId(Long fileId) {
        return selectOne(BabyAssessmentFileDO::getFileId, fileId);
    }

    default void deleteByFileId(Long fileId) {
        delete(BabyAssessmentFileDO::getFileId, fileId);
    }

    default List<BabyAssessmentFileDO> selectListByUploadUserId(Long uploadUserId) {
        return selectList(BabyAssessmentFileDO::getUploadUserId, uploadUserId);
    }

}
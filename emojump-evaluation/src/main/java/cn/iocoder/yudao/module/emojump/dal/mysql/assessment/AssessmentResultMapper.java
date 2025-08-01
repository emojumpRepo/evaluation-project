package cn.iocoder.yudao.module.emojump.dal.mysql.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.assessmentresult.vo.AssessmentResultPageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentResultDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 测评结果 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AssessmentResultMapper extends BaseMapperX<AssessmentResultDO> {

    default PageResult<AssessmentResultDO> selectPage(AssessmentResultPageReqVO reqVO) {
        return selectPageWithJoin(reqVO);
    }

    @Select("<script>" +
            "SELECT ar.* FROM emo_assessment_result ar " +
            "LEFT JOIN emo_assessment a ON ar.assessment_id = a.id " +
            "LEFT JOIN member_baby b ON ar.baby_id = b.id " +
            "WHERE 1=1 " +
            "<if test='reqVO.assessmentId != null'> AND ar.assessment_id = #{reqVO.assessmentId} </if>" +
            "<if test='reqVO.babyId != null'> AND ar.baby_id = #{reqVO.babyId} </if>" +
            "<if test='reqVO.status != null'> AND ar.status = #{reqVO.status} </if>" +
            "<if test='reqVO.assessmentTitle != null and reqVO.assessmentTitle != \"\"'> AND a.title LIKE CONCAT('%', #{reqVO.assessmentTitle}, '%') </if>" +
            "<if test='reqVO.babyName != null and reqVO.babyName != \"\"'> AND b.name LIKE CONCAT('%', #{reqVO.babyName}, '%') </if>" +
            "<if test='reqVO.completedTime != null and reqVO.completedTime.length == 2'> AND ar.completed_time BETWEEN #{reqVO.completedTime[0]} AND #{reqVO.completedTime[1]} </if>" +
            "ORDER BY ar.id DESC " +
            "LIMIT #{offset}, #{reqVO.pageSize}" +
            "</script>")
    List<AssessmentResultDO> selectPageWithJoinList(@Param("reqVO") AssessmentResultPageReqVO reqVO, @Param("offset") long offset);

    @Select("<script>" +
            "SELECT COUNT(1) FROM emo_assessment_result ar " +
            "LEFT JOIN emo_assessment a ON ar.assessment_id = a.id " +
            "LEFT JOIN member_baby b ON ar.baby_id = b.id " +
            "WHERE 1=1 " +
            "<if test='reqVO.assessmentId != null'> AND ar.assessment_id = #{reqVO.assessmentId} </if>" +
            "<if test='reqVO.babyId != null'> AND ar.baby_id = #{reqVO.babyId} </if>" +
            "<if test='reqVO.status != null'> AND ar.status = #{reqVO.status} </if>" +
            "<if test='reqVO.assessmentTitle != null and reqVO.assessmentTitle != \"\"'> AND a.title LIKE CONCAT('%', #{reqVO.assessmentTitle}, '%') </if>" +
            "<if test='reqVO.babyName != null and reqVO.babyName != \"\"'> AND b.name LIKE CONCAT('%', #{reqVO.babyName}, '%') </if>" +
            "<if test='reqVO.completedTime != null and reqVO.completedTime.length == 2'> AND ar.completed_time BETWEEN #{reqVO.completedTime[0]} AND #{reqVO.completedTime[1]} </if>" +
            "</script>")
    Long selectPageWithJoinCount(@Param("reqVO") AssessmentResultPageReqVO reqVO);

    default PageResult<AssessmentResultDO> selectPageWithJoin(AssessmentResultPageReqVO reqVO) {
        // 计算偏移量
        long offset = (long) (reqVO.getPageNo() - 1) * reqVO.getPageSize();
        List<AssessmentResultDO> list = selectPageWithJoinList(reqVO, offset);
        Long count = selectPageWithJoinCount(reqVO);
        return new PageResult<>(list, count);
    }

}

package cn.iocoder.yudao.module.emojump.dal.mysql.assessment;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.assessment.AssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.assessment.vo.AppAssessmentPageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.assessment.AssessmentDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AssessmentMapper extends BaseMapperX<AssessmentDO> {

    default PageResult<AssessmentDO> selectPage(AssessmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssessmentDO>()
                .likeIfPresent(AssessmentDO::getTitle, reqVO.getTitle())
                .eqIfPresent(AssessmentDO::getStatus, reqVO.getStatus())
                .eqIfPresent(AssessmentDO::getType, reqVO.getType())
                .betweenIfPresent(AssessmentDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AssessmentDO::getId));
    }

    default PageResult<AssessmentDO> selectPage(AppAssessmentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AssessmentDO>()
                .eqIfPresent(AssessmentDO::getType, reqVO.getType())
                .eqIfPresent(AssessmentDO::getStatus, reqVO.getStatus())
                .orderByDesc(AssessmentDO::getId));
    }

    default PageResult<AssessmentDO> selectPublishedPage(AppAssessmentPageReqVO reqVO) {
        // 创建分页对象
        Page<AssessmentDO> page = new Page<>(reqVO.getPageNo(), reqVO.getPageSize());
        
        // 构建查询条件
        LambdaQueryWrapperX<AssessmentDO> queryWrapper = new LambdaQueryWrapperX<AssessmentDO>()
                .eq(AssessmentDO::getStatus, 1) // 已发布状态
                .eqIfPresent(AssessmentDO::getType, reqVO.getType())
                .orderByDesc(AssessmentDO::getId);

        // 执行分页查询
        IPage<AssessmentDO> resultPage = selectPage(page, queryWrapper);
        
        // 转换为PageResult对象
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }
} 

package cn.iocoder.yudao.module.system.dal.mysql.platform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.controller.admin.platform.vo.policy.PolicyPageReqVO;
import cn.iocoder.yudao.module.system.dal.dataobject.platform.PolicyDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 政策配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface PolicyMapper extends BaseMapperX<PolicyDO> {

    default PageResult<PolicyDO> selectPage(PolicyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PolicyDO>()
                .likeIfPresent(PolicyDO::getTitle, reqVO.getTitle())
                .eqIfPresent(PolicyDO::getType, reqVO.getType())
                .eqIfPresent(PolicyDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(PolicyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(PolicyDO::getId));
    }

    default PolicyDO selectByType(String type) {
        return selectOne(PolicyDO::getType, type);
    }

}

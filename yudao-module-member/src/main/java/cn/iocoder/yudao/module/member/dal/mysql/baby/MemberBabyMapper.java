package cn.iocoder.yudao.module.member.dal.mysql.baby;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 宝宝信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberBabyMapper extends BaseMapperX<MemberBabyDO> {

    default PageResult<MemberBabyDO> selectPage(MemberBabyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberBabyDO>()
                .likeIfPresent(MemberBabyDO::getName, reqVO.getName())
                .eqIfPresent(MemberBabyDO::getGender, reqVO.getGender())
                .eqIfPresent(MemberBabyDO::getUserId, reqVO.getUserId())
                .orderByDesc(MemberBabyDO::getId));
    }

    default List<MemberBabyDO> selectListByUserId(Long userId) {
        return selectList(MemberBabyDO::getUserId, userId);
    }

} 
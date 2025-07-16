package cn.iocoder.yudao.module.member.dal.mysql.platform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.CarouselPageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.CarouselDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 轮播图 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CarouselMapper extends BaseMapperX<CarouselDO> {

    default PageResult<CarouselDO> selectPage(CarouselPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CarouselDO>()
                .likeIfPresent(CarouselDO::getTitle, reqVO.getTitle())
                .eqIfPresent(CarouselDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(CarouselDO::getCreateTime, reqVO.getCreateTime())
                .orderByAsc(CarouselDO::getSort)
                .orderByDesc(CarouselDO::getId));
    }

    default List<CarouselDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<CarouselDO>()
                .eqIfPresent(CarouselDO::getStatus, status)
                .orderByAsc(CarouselDO::getSort)
                .orderByDesc(CarouselDO::getId));
    }

}

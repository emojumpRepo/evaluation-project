package cn.iocoder.yudao.module.member.convert.platform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.CarouselCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.CarouselRespVO;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.CarouselUpdateReqVO;
import cn.iocoder.yudao.module.member.controller.app.platform.vo.AppCarouselRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.CarouselDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 轮播图 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface CarouselConvert {

    CarouselConvert INSTANCE = Mappers.getMapper(CarouselConvert.class);

    CarouselDO convert(CarouselCreateReqVO bean);

    CarouselDO convert(CarouselUpdateReqVO bean);

    CarouselRespVO convert(CarouselDO bean);

    List<CarouselRespVO> convertList(List<CarouselDO> list);

    PageResult<CarouselRespVO> convertPage(PageResult<CarouselDO> page);

    // ========== APP 相关 ==========

    AppCarouselRespVO convertApp(CarouselDO bean);

    List<AppCarouselRespVO> convertAppList(List<CarouselDO> list);

}

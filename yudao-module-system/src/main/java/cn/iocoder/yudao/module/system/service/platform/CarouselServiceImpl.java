package cn.iocoder.yudao.module.system.service.platform;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.platform.vo.carousel.CarouselCreateReqVO;
import cn.iocoder.yudao.module.system.controller.admin.platform.vo.carousel.CarouselPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.platform.vo.carousel.CarouselUpdateReqVO;
import cn.iocoder.yudao.module.system.convert.platform.CarouselConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.platform.CarouselDO;
import cn.iocoder.yudao.module.system.dal.mysql.platform.CarouselMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CAROUSEL_NOT_EXISTS;

/**
 * 轮播图 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class CarouselServiceImpl implements CarouselService {

    @Resource
    private CarouselMapper carouselMapper;

    @Override
    public Long createCarousel(@Valid CarouselCreateReqVO createReqVO) {
        // 插入
        CarouselDO carousel = CarouselConvert.INSTANCE.convert(createReqVO);
        carouselMapper.insert(carousel);
        // 返回
        return carousel.getId();
    }

    @Override
    public void updateCarousel(@Valid CarouselUpdateReqVO updateReqVO) {
        // 校验存在
        validateCarouselExists(updateReqVO.getId());
        // 更新
        CarouselDO updateObj = CarouselConvert.INSTANCE.convert(updateReqVO);
        carouselMapper.updateById(updateObj);
    }

    @Override
    public void deleteCarousel(Long id) {
        // 校验存在
        validateCarouselExists(id);
        // 删除
        carouselMapper.deleteById(id);
    }

    private void validateCarouselExists(Long id) {
        if (carouselMapper.selectById(id) == null) {
            throw exception(CAROUSEL_NOT_EXISTS);
        }
    }

    @Override
    public CarouselDO getCarousel(Long id) {
        return carouselMapper.selectById(id);
    }

    @Override
    public List<CarouselDO> getCarouselList(Collection<Long> ids) {
        return carouselMapper.selectByIds(ids);
    }

    @Override
    public PageResult<CarouselDO> getCarouselPage(CarouselPageReqVO pageReqVO) {
        return carouselMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CarouselDO> getEnabledCarouselList() {
        return carouselMapper.selectListByStatus(CommonStatusEnum.ENABLE.getStatus());
    }

}

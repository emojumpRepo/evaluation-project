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

import cn.iocoder.yudao.module.system.enums.platform.CarouselTypeEnum;
import org.springframework.util.StringUtils;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CAROUSEL_LINK_URL_NOT_NULL;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.CAROUSEL_POPUP_CONTENT_NOT_NULL;
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
        // 校验
        validateCarousel(createReqVO.getType(), createReqVO.getLinkUrl(), createReqVO.getPopupContent());
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
        // 校验
        validateCarousel(updateReqVO.getType(), updateReqVO.getLinkUrl(), updateReqVO.getPopupContent());
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

    private void validateCarousel(Integer type, String linkUrl, String popupContent) {
        if (type.equals(CarouselTypeEnum.REDIRECT.getType())) {
            if (!StringUtils.hasText(linkUrl)) {
                throw exception(CAROUSEL_LINK_URL_NOT_NULL);
            }
        } else if (type.equals(CarouselTypeEnum.POPUP.getType())) {
            if (!StringUtils.hasText(popupContent)) {
                throw exception(CAROUSEL_POPUP_CONTENT_NOT_NULL);
            }
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

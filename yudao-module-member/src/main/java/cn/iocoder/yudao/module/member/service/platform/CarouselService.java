package cn.iocoder.yudao.module.member.service.platform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.CarouselCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.CarouselPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.CarouselUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.CarouselDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 轮播图 Service 接口
 *
 * @author 芋道源码
 */
public interface CarouselService {

    /**
     * 创建轮播图
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCarousel(@Valid CarouselCreateReqVO createReqVO);

    /**
     * 更新轮播图
     *
     * @param updateReqVO 更新信息
     */
    void updateCarousel(@Valid CarouselUpdateReqVO updateReqVO);

    /**
     * 删除轮播图
     *
     * @param id 编号
     */
    void deleteCarousel(Long id);

    /**
     * 获得轮播图
     *
     * @param id 编号
     * @return 轮播图
     */
    CarouselDO getCarousel(Long id);

    /**
     * 获得轮播图列表
     *
     * @param ids 编号
     * @return 轮播图列表
     */
    List<CarouselDO> getCarouselList(Collection<Long> ids);

    /**
     * 获得轮播图分页
     *
     * @param pageReqVO 分页查询
     * @return 轮播图分页
     */
    PageResult<CarouselDO> getCarouselPage(CarouselPageReqVO pageReqVO);

    /**
     * 获得启用状态的轮播图列表
     *
     * @return 轮播图列表
     */
    List<CarouselDO> getEnabledCarouselList();

}

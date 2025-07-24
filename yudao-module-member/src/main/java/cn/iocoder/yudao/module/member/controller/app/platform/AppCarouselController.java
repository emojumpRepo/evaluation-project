package cn.iocoder.yudao.module.member.controller.app.platform;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.platform.vo.AppCarouselRespVO;
import cn.iocoder.yudao.module.member.convert.platform.CarouselConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.CarouselDO;
import cn.iocoder.yudao.module.member.service.platform.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 轮播图")
@RestController
@RequestMapping("/member/carousel")
@Validated
public class AppCarouselController {

    @Resource
    private CarouselService carouselService;

    @GetMapping("/list")
    @Operation(summary = "获得启用的轮播图列表")
    @PermitAll
    public CommonResult<List<AppCarouselRespVO>> getEnabledCarouselList() {
        List<CarouselDO> list = carouselService.getEnabledCarouselList();
        return success(CarouselConvert.INSTANCE.convertAppList(list));
    }

}

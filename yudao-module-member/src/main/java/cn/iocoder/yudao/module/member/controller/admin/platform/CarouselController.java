package cn.iocoder.yudao.module.member.controller.admin.platform;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.carousel.*;
import cn.iocoder.yudao.module.member.convert.platform.CarouselConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.CarouselDO;
import cn.iocoder.yudao.module.member.service.platform.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 轮播图")
@RestController
@RequestMapping("/member/carousel")
@Validated
public class CarouselController {

    @Resource
    private CarouselService carouselService;

    @PostMapping("/create")
    @Operation(summary = "创建轮播图")
    @PreAuthorize("@ss.hasPermission('member:carousel:create')")
    public CommonResult<Long> createCarousel(@Valid @RequestBody CarouselCreateReqVO createReqVO) {
        return success(carouselService.createCarousel(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新轮播图")
    @PreAuthorize("@ss.hasPermission('member:carousel:update')")
    public CommonResult<Boolean> updateCarousel(@Valid @RequestBody CarouselUpdateReqVO updateReqVO) {
        carouselService.updateCarousel(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除轮播图")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('member:carousel:delete')")
    public CommonResult<Boolean> deleteCarousel(@RequestParam("id") Long id) {
        carouselService.deleteCarousel(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得轮播图")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('member:carousel:query')")
    public CommonResult<CarouselRespVO> getCarousel(@RequestParam("id") Long id) {
        CarouselDO carousel = carouselService.getCarousel(id);
        return success(CarouselConvert.INSTANCE.convert(carousel));
    }

    @GetMapping("/list")
    @Operation(summary = "获得轮播图列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('member:carousel:query')")
    public CommonResult<List<CarouselRespVO>> getCarouselList(@RequestParam("ids") Collection<Long> ids) {
        List<CarouselDO> list = carouselService.getCarouselList(ids);
        return success(CarouselConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得轮播图分页")
    @PreAuthorize("@ss.hasPermission('member:carousel:query')")
    public CommonResult<PageResult<CarouselRespVO>> getCarouselPage(@Valid CarouselPageReqVO pageVO) {
        PageResult<CarouselDO> pageResult = carouselService.getCarouselPage(pageVO);
        return success(CarouselConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出轮播图 Excel")
    @PreAuthorize("@ss.hasPermission('member:carousel:export')")
    public void exportCarouselExcel(@Valid CarouselPageReqVO pageVO,
              HttpServletResponse response) throws IOException {
        pageVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CarouselDO> list = carouselService.getCarouselPage(pageVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "轮播图.xls", "数据", CarouselRespVO.class,
                        CarouselConvert.INSTANCE.convertList(list));
    }

}

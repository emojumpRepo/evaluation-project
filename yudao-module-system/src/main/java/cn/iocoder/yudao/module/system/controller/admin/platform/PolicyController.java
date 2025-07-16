package cn.iocoder.yudao.module.system.controller.admin.platform;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;
import cn.iocoder.yudao.module.system.controller.admin.platform.vo.policy.*;
import cn.iocoder.yudao.module.system.convert.platform.PolicyConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.platform.PolicyDO;
import cn.iocoder.yudao.module.system.service.platform.PolicyService;
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

@Tag(name = "管理后台 - 政策配置")
@RestController
@RequestMapping("/system/policy")
@Validated
public class PolicyController {

    @Resource
    private PolicyService policyService;

    @PutMapping("/update")
    @Operation(summary = "更新政策配置")
    @PreAuthorize("@ss.hasPermission('system:policy:update')")
    public CommonResult<Boolean> updatePolicy(@Valid @RequestBody PolicyUpdateReqVO updateReqVO) {
        policyService.updatePolicy(updateReqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得政策配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:policy:query')")
    public CommonResult<PolicyRespVO> getPolicy(@RequestParam("id") Long id) {
        PolicyDO policy = policyService.getPolicy(id);
        return success(PolicyConvert.INSTANCE.convert(policy));
    }

    @GetMapping("/get-by-type")
    @Operation(summary = "根据类型获得政策配置")
    @Parameter(name = "type", description = "政策类型", required = true, example = "service_agreement")
    @PreAuthorize("@ss.hasPermission('system:policy:query')")
    public CommonResult<PolicyRespVO> getPolicyByType(@RequestParam("type") String type) {
        PolicyDO policy = policyService.getPolicyByType(type);
        return success(PolicyConvert.INSTANCE.convert(policy));
    }

    @GetMapping("/list")
    @Operation(summary = "获得政策配置列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    @PreAuthorize("@ss.hasPermission('system:policy:query')")
    public CommonResult<List<PolicyRespVO>> getPolicyList(@RequestParam("ids") Collection<Long> ids) {
        List<PolicyDO> list = policyService.getPolicyList(ids);
        return success(PolicyConvert.INSTANCE.convertList(list));
    }

    @GetMapping("/page")
    @Operation(summary = "获得政策配置分页")
    @PreAuthorize("@ss.hasPermission('system:policy:query')")
    public CommonResult<PageResult<PolicyRespVO>> getPolicyPage(@Valid PolicyPageReqVO pageVO) {
        PageResult<PolicyDO> pageResult = policyService.getPolicyPage(pageVO);
        return success(PolicyConvert.INSTANCE.convertPage(pageResult));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出政策配置 Excel")
    @PreAuthorize("@ss.hasPermission('system:policy:export')")
    public void exportPolicyExcel(@Valid PolicyPageReqVO pageVO,
              HttpServletResponse response) throws IOException {
        pageVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PolicyDO> list = policyService.getPolicyPage(pageVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "政策配置.xls", "数据", PolicyRespVO.class,
                        PolicyConvert.INSTANCE.convertList(list));
    }

}

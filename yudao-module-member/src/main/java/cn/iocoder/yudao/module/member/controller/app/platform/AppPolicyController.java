package cn.iocoder.yudao.module.member.controller.app.platform;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.platform.vo.AppPolicyRespVO;
import cn.iocoder.yudao.module.member.convert.platform.PolicyConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.PolicyDO;
import cn.iocoder.yudao.module.member.service.platform.PolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.annotation.security.PermitAll;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 政策配置")
@RestController
@RequestMapping("/member/policy")
@Validated
public class AppPolicyController {

    @Resource
    private PolicyService policyService;

    @GetMapping("/get-by-type")
    @Operation(summary = "根据类型获得政策配置")
    @Parameter(name = "type", description = "政策类型", required = true, example = "service_agreement")
    @PermitAll
    public CommonResult<AppPolicyRespVO> getPolicyByType(@RequestParam("type") String type) {
        PolicyDO policy = policyService.getPolicyByType(type);
        return success(PolicyConvert.INSTANCE.convertApp(policy));
    }

}

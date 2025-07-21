package cn.iocoder.yudao.module.member.controller.admin.baby;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyRespVO;
import cn.iocoder.yudao.module.member.convert.baby.MemberBabyConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.member.service.baby.MemberBabyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 宝宝信息")
@RestController
@RequestMapping("/member/baby")
@Validated
public class MemberBabyController {

    @Resource
    private MemberBabyService babyService;

    @DeleteMapping("/delete")
    @Operation(summary = "删除宝宝信息")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteBaby(@RequestParam("id") Long id) {
        babyService.deleteBaby(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获得宝宝信息列表")
    public CommonResult<PageResult<MemberBabyRespVO>> getBabyList(@Valid MemberBabyPageReqVO pageVO) {
        PageResult<MemberBabyDO> pageResult = babyService.getBabyList(pageVO);
        return success(MemberBabyConvert.INSTANCE.convertPage(pageResult));
    }

} 
package cn.iocoder.yudao.module.member.controller.app.baby;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.*;
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
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 宝宝信息")
@RestController
@RequestMapping("/member/baby")
@Validated
public class AppMemberBabyController {

    @Resource
    private MemberBabyService babyService;

    @PostMapping("/create")
    @Operation(summary = "创建宝宝信息")
    public CommonResult<Long> createBaby(@Valid @RequestBody AppMemberBabyCreateReqVO createReqVO) {
        return success(babyService.createBaby(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新宝宝信息")
    public CommonResult<Boolean> updateBaby(@Valid @RequestBody AppMemberBabyUpdateReqVO updateReqVO) {
        babyService.updateBaby(updateReqVO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得宝宝信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<AppMemberBabyRespVO> getBaby(@RequestParam("id") Long id) {
        MemberBabyDO baby = babyService.getBaby(id);
        return success(MemberBabyConvert.INSTANCE.convertApp(baby));
    }

    @GetMapping("/list-by-user-id")
    @Operation(summary = "根据用户编号获得宝宝信息列表")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1024")
    public CommonResult<List<AppMemberBabyRespVO>> getBabyListByUserId(@RequestParam("userId") Long userId) {
        List<MemberBabyDO> list = babyService.getBabyListByUserId(userId);
        return success(MemberBabyConvert.INSTANCE.convertAppList(list));
    }

} 
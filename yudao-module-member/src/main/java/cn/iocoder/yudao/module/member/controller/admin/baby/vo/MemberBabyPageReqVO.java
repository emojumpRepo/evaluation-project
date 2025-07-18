package cn.iocoder.yudao.module.member.controller.admin.baby.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Schema(description = "管理后台 - 宝宝信息分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MemberBabyPageReqVO extends PageParam {

    @Schema(description = "宝宝姓名", example = "小明")
    private String name;

    @Schema(description = "性别", example = "1")
    private Integer gender;

    @Schema(description = "用户ID", example = "1024")
    private Long userId;

    @Schema(description = "用户手机号", example = "15888888888")
    private String mobile;
} 
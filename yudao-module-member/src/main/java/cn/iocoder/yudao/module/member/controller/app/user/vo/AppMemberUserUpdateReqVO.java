package cn.iocoder.yudao.module.member.controller.app.user.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.common.SexEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Schema(description = "用户 App - 会员用户更新 Request VO")
@Data
public class AppMemberUserUpdateReqVO {

    // @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    // private String nickname;

    @Schema(description = "头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/x.png")
    @URL(message = "头像必须是 URL 格式")
    private String avatar;

    @Schema(description = "性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sex;

    @Schema(description = "真实名字", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String name;

    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED, example = "13011235432")
    private String mobile;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED, example = "130101199003074919")
    private String idCard;

    @Schema(description = "家庭角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer familyRole;

    @Schema(description = "联系地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京市海淀区")
    private String address;

    @Schema(description = "所属学校", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京大学")
    private String school;

    @Schema(description = "血型", requiredMode = Schema.RequiredMode.REQUIRED, example = "A")
    private String bloodType;

    @Schema(description = "身高", requiredMode = Schema.RequiredMode.REQUIRED, example = "180")
    private Integer height;

    @Schema(description = "体重", requiredMode = Schema.RequiredMode.REQUIRED, example = "70")
    private Integer weight;
}

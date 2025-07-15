package cn.iocoder.yudao.module.member.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "用户 APP - 用户个人信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppMemberUserInfoRespVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    private String nickname;

    @Schema(description = "用户真实名字", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    private String name;

    @Schema(description = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn/xxx.png")
    private String avatar;

    @Schema(description = "用户手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    private String mobile;

    @Schema(description = "用户性别", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer sex;

    @Schema(description = "用户身份证号", requiredMode = Schema.RequiredMode.REQUIRED, example = "130101199003074919")
    private String idCard;

    @Schema(description = "用户家庭角色", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer familyRole;

    @Schema(description = "用户联系地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京市海淀区")
    private String address;

    @Schema(description = "用户所属学校", requiredMode = Schema.RequiredMode.REQUIRED, example = "北京大学")
    private String school;

    @Schema(description = "用户血型", requiredMode = Schema.RequiredMode.REQUIRED, example = "A")
    private String bloodType;

    @Schema(description = "用户身高", requiredMode = Schema.RequiredMode.REQUIRED, example = "180")
    private Integer height;

    @Schema(description = "用户体重", requiredMode = Schema.RequiredMode.REQUIRED, example = "70")
    private Integer weight;

    @Schema(description = "积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer point;

    @Schema(description = "经验值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer experience;

    @Schema(description = "用户等级")
    private Level level;

    @Schema(description = "是否成为推广员", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean brokerageEnabled;

    @Schema(description = "用户 App - 会员等级")
    @Data
    public static class Level {

        @Schema(description = "等级编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "等级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
        private String name;

        @Schema(description = "等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Integer level;

        @Schema(description = "等级图标", example = "https://www.iocoder.cn/yudao.jpg")
        private String icon;

    }

}

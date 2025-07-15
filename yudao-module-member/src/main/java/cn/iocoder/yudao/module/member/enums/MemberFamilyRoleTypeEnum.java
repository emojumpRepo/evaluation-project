package cn.iocoder.yudao.module.member.enums;

import cn.hutool.core.util.EnumUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 会员家庭角色 - 业务类型
 *
 * @author Swance
 */
@Getter
@AllArgsConstructor
public enum MemberFamilyRoleTypeEnum {

    /** 父亲 */
    FATHER(1),
    /** 母亲 */
    MOTHER(2),
    /* 未知 */
    UNKNOWN(0);

    /**
     * 家庭角色
     */
    private final Integer familyRole;
}

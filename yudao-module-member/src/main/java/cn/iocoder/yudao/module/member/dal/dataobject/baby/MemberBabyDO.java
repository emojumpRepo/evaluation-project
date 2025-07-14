package cn.iocoder.yudao.module.member.dal.dataobject.baby;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 宝宝信息 DO
 *
 * @author 芋道源码
 */
@TableName(value = "member_baby", autoResultMap = true)
@KeySequence("member_baby_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberBabyDO extends TenantBaseDO {

    /**
     * 宝宝ID
     */
    @TableId
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 宝宝姓名
     */
    private String name;
    
    /**
     * 宝宝生日
     */
    private LocalDate birthday;
    
    /**
     * 性别
     * 
     * 枚举 {@link cn.iocoder.yudao.module.system.enums.common.SexEnum}
     * 1-男，2-女
     */
    private Integer gender;
    
    /**
     * 身高(cm)
     */
    private BigDecimal height;
    
    /**
     * 体重(kg)
     */
    private BigDecimal weight;
    
    /**
     * 生育方式
     */
    private String birthType;
    
    /**
     * 监护人
     */
    private String guardian;
    
    /**
     * 宝宝头像
     */
    private String avatar;

} 
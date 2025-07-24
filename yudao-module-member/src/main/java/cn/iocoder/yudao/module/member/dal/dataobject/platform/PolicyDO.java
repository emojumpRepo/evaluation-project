package cn.iocoder.yudao.module.member.dal.dataobject.platform;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 政策配置 DO
 *
 * @author 芋道源码
 */
@TableName("member_policy")
@KeySequence("member_policy_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDO extends TenantBaseDO {

    /**
     * 政策ID
     */
    @TableId
    private Long id;
    
    /**
     * 政策类型
     * 
     * 枚举 {@link cn.iocoder.yudao.module.member.enums.platform.PolicyTypeEnum}
     */
    private String type;
    
    /**
     * 政策标题
     */
    private String title;
    
    /**
     * 政策内容（富文本）
     */
    private String content;
    
    /**
     * 版本号
     */
    private String version;
    
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    
    /**
     * 生效时间
     */
    private LocalDateTime effectiveTime;
    
    /**
     * 备注
     */
    private String remark;

}

package cn.iocoder.yudao.module.system.dal.dataobject.platform;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 轮播图 DO
 *
 * @author 芋道源码
 */
@TableName("system_carousel")
@KeySequence("system_carousel_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarouselDO extends TenantBaseDO {

    /**
     * 轮播图ID
     */
    @TableId
    private Long id;
    
    /**
     * 轮播图标题
     */
    private String title;
    
    /**
     * 图片URL
     */
    private String imageUrl;
    
    /**
     * 跳转链接
     */
    private String linkUrl;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;

}

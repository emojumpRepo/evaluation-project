package cn.iocoder.yudao.module.emojump.dal.dataobject.article;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 文章分类 DO
 *
 * @author 芋道源码
 */
@TableName(value = "emojump_article_category", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCategoryDO extends TenantBaseDO {

    /**
     * 分类ID
     */
    @TableId
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

}


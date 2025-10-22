package cn.iocoder.yudao.module.emojump.dal.dataobject.article;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员文章 DO
 *
 * @author 芋道源码
 */
@TableName(value = "emojump_article", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDO extends TenantBaseDO {

    /**
     * 文章ID
     */
    @TableId
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章封面图片URL
     */
    private String coverImage;

    /**
     * 文章分类ID
     */
    private Long categoryId;

    /**
     * 文章状态
     * 0-草稿 1-已发布 2-已下线
     */
    private Integer status;

    /**
     * 阅读量
     */
    private Integer viewCount;

    /**
     * 点赞数
     */
    private Integer likeCount;

    /**
     * 发布时间（时间戳）
     */
    private Long publishTime;

    /**
     * 备注
     */
    private String remark;

} 
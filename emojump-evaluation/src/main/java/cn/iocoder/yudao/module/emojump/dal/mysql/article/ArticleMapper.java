package cn.iocoder.yudao.module.emojump.dal.mysql.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.ArticlePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.article.vo.AppArticlePageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ArticleMapper extends BaseMapperX<ArticleDO> {

    /**
     * 管理端分页查询
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<ArticleDO> selectPage(ArticlePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ArticleDO>()
                .likeIfPresent(ArticleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ArticleDO::getCategoryId, reqVO.getCategoryId())
                .eqIfPresent(ArticleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(ArticleDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(ArticleDO::getPublishTime, reqVO.getPublishTime())
                .orderByDesc(ArticleDO::getId));
    }

    /**
     * App端分页查询（只查询已发布的文章）
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<ArticleDO> selectAppPage(AppArticlePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ArticleDO>()
                .eq(ArticleDO::getStatus, 1) // 只查询已发布的文章
                .likeIfPresent(ArticleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(ArticleDO::getCategoryId, reqVO.getCategoryId())
                .orderByDesc(ArticleDO::getId));
    }

    /**
     * App端根据ID查询文章（只查询已发布的文章）
     *
     * @param id 文章ID
     * @return 文章信息
     */
    default ArticleDO selectByIdAndStatus(Long id) {
        return selectOne(new LambdaQueryWrapperX<ArticleDO>()
                .eq(ArticleDO::getId, id)
                .eq(ArticleDO::getStatus, 1)); // 只查询已发布的文章
    }

} 
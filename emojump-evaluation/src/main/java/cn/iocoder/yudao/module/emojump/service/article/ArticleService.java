package cn.iocoder.yudao.module.emojump.service.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.ArticleCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.ArticlePageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.ArticleUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.article.vo.AppArticlePageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleDO;

import javax.validation.Valid;

/**
 * 会员文章 Service 接口
 *
 * @author 芋道源码
 */
public interface ArticleService {

    /**
     * 创建会员文章
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createArticle(@Valid ArticleCreateReqVO createReqVO);

    /**
     * 更新会员文章
     *
     * @param updateReqVO 更新信息
     */
    void updateArticle(@Valid ArticleUpdateReqVO updateReqVO);

    /**
     * 删除会员文章
     *
     * @param id 编号
     */
    void deleteArticle(Long id);

    /**
     * 获得会员文章
     *
     * @param id 编号
     * @return 会员文章
     */
    ArticleDO getArticle(Long id);

    /**
     * 获得会员文章分页
     *
     * @param pageReqVO 分页查询
     * @return 会员文章分页
     */
    PageResult<ArticleDO> getArticlePage(ArticlePageReqVO pageReqVO);

    /**
     * App端获得会员文章分页（只获取已发布的文章）
     *
     * @param pageReqVO 分页查询
     * @return 会员文章分页
     */
    PageResult<ArticleDO> getAppArticlePage(AppArticlePageReqVO pageReqVO);

    /**
     * App端获得会员文章详情（只获取已发布的文章）
     *
     * @param id 编号
     * @return 会员文章
     */
    ArticleDO getAppArticle(Long id);

    /**
     * 增加文章阅读量
     *
     * @param id 文章编号
     */
    void increaseViewCount(Long id);

    /**
     * 增加文章点赞量
     *
     * @param id 文章编号
     */
    void increaseLikeCount(Long id);

} 
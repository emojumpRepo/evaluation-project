package cn.iocoder.yudao.module.emojump.service.articlecategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryUpdateReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleCategoryDO;

import javax.validation.Valid;
import java.util.List;

/**
 * 文章分类 Service 接口
 *
 * @author 芋道源码
 */
public interface ArticleCategoryService {

    /**
     * 创建文章分类
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createArticleCategory(@Valid ArticleCategoryCreateReqVO createReqVO);

    /**
     * 更新文章分类
     *
     * @param updateReqVO 更新信息
     */
    void updateArticleCategory(@Valid ArticleCategoryUpdateReqVO updateReqVO);

    /**
     * 删除文章分类
     *
     * @param id 编号
     */
    void deleteArticleCategory(Long id);

    /**
     * 获得文章分类
     *
     * @param id 编号
     * @return 文章分类
     */
    ArticleCategoryDO getArticleCategory(Long id);

    /**
     * 获得文章分类分页
     *
     * @param pageReqVO 分页查询
     * @return 文章分类分页
     */
    PageResult<ArticleCategoryDO> getArticleCategoryPage(ArticleCategoryPageReqVO pageReqVO);

    /**
     * 获得所有文章分类列表
     *
     * @return 文章分类列表
     */
    List<ArticleCategoryDO> getArticleCategoryListAll();

}


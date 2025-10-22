package cn.iocoder.yudao.module.emojump.service.articlecategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryPageReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryUpdateReqVO;
import cn.iocoder.yudao.module.emojump.convert.articlecategory.ArticleCategoryConvert;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleCategoryDO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleDO;
import cn.iocoder.yudao.module.emojump.dal.mysql.article.ArticleCategoryMapper;
import cn.iocoder.yudao.module.emojump.dal.mysql.article.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emojump.enums.ErrorCodeConstants.*;

/**
 * 文章分类 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class ArticleCategoryServiceImpl implements ArticleCategoryService {

    @Resource
    private ArticleCategoryMapper articleCategoryMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Long createArticleCategory(ArticleCategoryCreateReqVO createReqVO) {
        // 校验分类名称唯一性
        validateCategoryNameUnique(null, createReqVO.getName());

        // 插入
        ArticleCategoryDO category = ArticleCategoryConvert.INSTANCE.convert(createReqVO);
        articleCategoryMapper.insert(category);

        // 返回
        return category.getId();
    }

    @Override
    public void updateArticleCategory(ArticleCategoryUpdateReqVO updateReqVO) {
        // 校验存在
        validateArticleCategoryExists(updateReqVO.getId());

        // 校验分类名称唯一性
        validateCategoryNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 更新
        ArticleCategoryDO updateObj = ArticleCategoryConvert.INSTANCE.convert(updateReqVO);
        articleCategoryMapper.updateById(updateObj);
    }

    @Override
    public void deleteArticleCategory(Long id) {
        // 校验存在
        validateArticleCategoryExists(id);

        // 校验是否有文章使用该分类
        validateCategoryNotUsed(id);

        // 删除
        articleCategoryMapper.deleteById(id);
    }

    private void validateArticleCategoryExists(Long id) {
        if (articleCategoryMapper.selectById(id) == null) {
            throw exception(ARTICLE_CATEGORY_NOT_EXISTS);
        }
    }

    private void validateCategoryNameUnique(Long id, String name) {
        ArticleCategoryDO category = articleCategoryMapper.selectByName(name);
        if (category == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的分类
        if (id == null) {
            throw exception(ARTICLE_CATEGORY_NAME_DUPLICATE);
        }
        if (!category.getId().equals(id)) {
            throw exception(ARTICLE_CATEGORY_NAME_DUPLICATE);
        }
    }

    private void validateCategoryNotUsed(Long id) {
        // 检查是否有文章使用该分类
        Long count = articleMapper.selectCount(ArticleDO::getCategoryId, id);
        if (count > 0) {
            throw exception(ARTICLE_CATEGORY_HAS_ARTICLES);
        }
    }

    @Override
    public ArticleCategoryDO getArticleCategory(Long id) {
        return articleCategoryMapper.selectById(id);
    }

    @Override
    public PageResult<ArticleCategoryDO> getArticleCategoryPage(ArticleCategoryPageReqVO pageReqVO) {
        return articleCategoryMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ArticleCategoryDO> getArticleCategoryListAll() {
        return articleCategoryMapper.selectListAll();
    }

}


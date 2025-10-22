package cn.iocoder.yudao.module.emojump.dal.mysql.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryPageReqVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文章分类 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ArticleCategoryMapper extends BaseMapperX<ArticleCategoryDO> {

    /**
     * 分页查询
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<ArticleCategoryDO> selectPage(ArticleCategoryPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ArticleCategoryDO>()
                .likeIfPresent(ArticleCategoryDO::getName, reqVO.getName())
                .orderByAsc(ArticleCategoryDO::getSort)
                .orderByDesc(ArticleCategoryDO::getId));
    }

    /**
     * 根据名称查询
     *
     * @param name 分类名称
     * @return 分类信息
     */
    default ArticleCategoryDO selectByName(String name) {
        return selectOne(ArticleCategoryDO::getName, name);
    }

    /**
     * 查询所有分类列表（按排序升序）
     *
     * @return 分类列表
     */
    default List<ArticleCategoryDO> selectListAll() {
        return selectList(new LambdaQueryWrapperX<ArticleCategoryDO>()
                .orderByAsc(ArticleCategoryDO::getSort)
                .orderByDesc(ArticleCategoryDO::getId));
    }

}


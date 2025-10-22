package cn.iocoder.yudao.module.emojump.convert.articlecategory;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategorySimpleRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.articlecategory.vo.ArticleCategoryUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.articlecategory.vo.AppArticleCategoryRespVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文章分类 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ArticleCategoryConvert {

    ArticleCategoryConvert INSTANCE = Mappers.getMapper(ArticleCategoryConvert.class);

    @Mapping(target = "id", ignore = true)
    ArticleCategoryDO convert(ArticleCategoryCreateReqVO bean);

    ArticleCategoryDO convert(ArticleCategoryUpdateReqVO bean);

    ArticleCategoryRespVO convert(ArticleCategoryDO bean);

    List<ArticleCategoryRespVO> convertList(List<ArticleCategoryDO> list);

    PageResult<ArticleCategoryRespVO> convertPage(PageResult<ArticleCategoryDO> page);

    List<ArticleCategorySimpleRespVO> convertSimpleList(List<ArticleCategoryDO> list);

    List<AppArticleCategoryRespVO> convertAppList(List<ArticleCategoryDO> list);

}


package cn.iocoder.yudao.module.emojump.convert.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.ArticleCreateReqVO;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.ArticleRespVO;
import cn.iocoder.yudao.module.emojump.controller.admin.article.vo.ArticleUpdateReqVO;
import cn.iocoder.yudao.module.emojump.controller.app.article.vo.AppArticleRespVO;
import cn.iocoder.yudao.module.emojump.dal.dataobject.article.ArticleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员文章 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ArticleConvert {

    ArticleConvert INSTANCE = Mappers.getMapper(ArticleConvert.class);

    ArticleDO convert(ArticleCreateReqVO bean);

    ArticleDO convert(ArticleUpdateReqVO bean);

    ArticleRespVO convert(ArticleDO bean);

    List<ArticleRespVO> convertList(List<ArticleDO> list);

    PageResult<ArticleRespVO> convertPage(PageResult<ArticleDO> page);

    AppArticleRespVO convertApp(ArticleDO bean);

    List<AppArticleRespVO> convertAppList(List<ArticleDO> list);

    PageResult<AppArticleRespVO> convertAppPage(PageResult<ArticleDO> page);

} 
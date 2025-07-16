package cn.iocoder.yudao.module.member.convert.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.MemberArticleCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.MemberArticleRespVO;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.MemberArticleUpdateReqVO;
import cn.iocoder.yudao.module.member.controller.app.article.vo.AppMemberArticleRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.article.MemberArticleDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员文章 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberArticleConvert {

    MemberArticleConvert INSTANCE = Mappers.getMapper(MemberArticleConvert.class);

    MemberArticleDO convert(MemberArticleCreateReqVO bean);

    MemberArticleDO convert(MemberArticleUpdateReqVO bean);

    MemberArticleRespVO convert(MemberArticleDO bean);

    List<MemberArticleRespVO> convertList(List<MemberArticleDO> list);

    PageResult<MemberArticleRespVO> convertPage(PageResult<MemberArticleDO> page);

    AppMemberArticleRespVO convertApp(MemberArticleDO bean);

    List<AppMemberArticleRespVO> convertAppList(List<MemberArticleDO> list);

    PageResult<AppMemberArticleRespVO> convertAppPage(PageResult<MemberArticleDO> page);

} 
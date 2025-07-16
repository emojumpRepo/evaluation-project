package cn.iocoder.yudao.module.member.dal.mysql.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.MemberArticlePageReqVO;
import cn.iocoder.yudao.module.member.controller.app.article.vo.AppMemberArticlePageReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.article.MemberArticleDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员文章 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberArticleMapper extends BaseMapperX<MemberArticleDO> {

    /**
     * 管理端分页查询
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<MemberArticleDO> selectPage(MemberArticlePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberArticleDO>()
                .likeIfPresent(MemberArticleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(MemberArticleDO::getCategory, reqVO.getCategory())
                .eqIfPresent(MemberArticleDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(MemberArticleDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(MemberArticleDO::getPublishTime, reqVO.getPublishTime())
                .orderByDesc(MemberArticleDO::getPublishTime)
                .orderByDesc(MemberArticleDO::getId));
    }

    /**
     * App端分页查询（只查询已发布的文章）
     *
     * @param reqVO 查询条件
     * @return 分页结果
     */
    default PageResult<MemberArticleDO> selectAppPage(AppMemberArticlePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MemberArticleDO>()
                .eq(MemberArticleDO::getStatus, 1) // 只查询已发布的文章
                .likeIfPresent(MemberArticleDO::getTitle, reqVO.getTitle())
                .eqIfPresent(MemberArticleDO::getCategory, reqVO.getCategory())
                .orderByDesc(MemberArticleDO::getPublishTime)); // 按发布时间降序
    }

    /**
     * App端根据ID查询文章（只查询已发布的文章）
     *
     * @param id 文章ID
     * @return 文章信息
     */
    default MemberArticleDO selectByIdAndStatus(Long id) {
        return selectOne(new LambdaQueryWrapperX<MemberArticleDO>()
                .eq(MemberArticleDO::getId, id)
                .eq(MemberArticleDO::getStatus, 1)); // 只查询已发布的文章
    }

} 
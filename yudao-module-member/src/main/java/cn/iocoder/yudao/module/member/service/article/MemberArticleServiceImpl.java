package cn.iocoder.yudao.module.member.service.article;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.MemberArticleCreateReqVO;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.MemberArticlePageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.article.vo.MemberArticleUpdateReqVO;
import cn.iocoder.yudao.module.member.controller.app.article.vo.AppMemberArticlePageReqVO;
import cn.iocoder.yudao.module.member.convert.article.MemberArticleConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.article.MemberArticleDO;
import cn.iocoder.yudao.module.member.dal.mysql.article.MemberArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.ARTICLE_NOT_EXISTS;
import java.time.LocalDateTime;

/**
 * 会员文章 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MemberArticleServiceImpl implements MemberArticleService {

    @Resource
    private MemberArticleMapper articleMapper;

    @Override
    public Long createArticle(MemberArticleCreateReqVO createReqVO) {
        // 插入
        MemberArticleDO article = MemberArticleConvert.INSTANCE.convert(createReqVO);
        // 设置默认值
        if (article.getViewCount() == null) {
            article.setViewCount(0);
        }
        if (article.getLikeCount() == null) {
            article.setLikeCount(0);
        }
        // 当文章状态为已发布且发布时间为空时，设置为当前时间戳
        if (article.getStatus() != null && article.getStatus() == 1 && article.getPublishTime() == null) {
            article.setPublishTime(System.currentTimeMillis());
        }
        articleMapper.insert(article);
        // 返回
        return article.getId();
    }

    @Override
    public void updateArticle(MemberArticleUpdateReqVO updateReqVO) {
        // 校验存在并获取原文章信息
        MemberArticleDO existingArticle = articleMapper.selectById(updateReqVO.getId());
        if (existingArticle == null) {
            throw exception(ARTICLE_NOT_EXISTS);
        }
        
        // 更新
        MemberArticleDO updateObj = MemberArticleConvert.INSTANCE.convert(updateReqVO);
        
        // 如果文章状态改为已发布且发布时间为空，设置为当前时间戳
        if (updateObj.getStatus() != null && updateObj.getStatus() == 1 && 
            updateObj.getPublishTime() == null && existingArticle.getPublishTime() == null) {
            updateObj.setPublishTime(System.currentTimeMillis());
        }
        
        articleMapper.updateById(updateObj);
    }

    @Override
    public void deleteArticle(Long id) {
        // 校验存在
        validateArticleExists(id);
        // 删除
        articleMapper.deleteById(id);
    }

    private void validateArticleExists(Long id) {
        if (articleMapper.selectById(id) == null) {
            throw exception(ARTICLE_NOT_EXISTS);
        }
    }

    @Override
    public MemberArticleDO getArticle(Long id) {
        return articleMapper.selectById(id);
    }

    @Override
    public PageResult<MemberArticleDO> getArticlePage(MemberArticlePageReqVO pageReqVO) {
        return articleMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<MemberArticleDO> getAppArticlePage(AppMemberArticlePageReqVO pageReqVO) {
        return articleMapper.selectAppPage(pageReqVO);
    }

    @Override
    public MemberArticleDO getAppArticle(Long id) {
        MemberArticleDO article = articleMapper.selectByIdAndStatus(id);
        if (article != null) {
            // 增加阅读量
            increaseViewCount(id);
        }
        return article;
    }

    @Override
    public void increaseViewCount(Long id) {
        // 增加阅读量
        MemberArticleDO article = articleMapper.selectById(id);
        if (article != null) {
            article.setViewCount(article.getViewCount() + 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    public void increaseLikeCount(Long id) {
        // 增加点赞量
        MemberArticleDO article = articleMapper.selectById(id);
        if (article != null) {
            article.setLikeCount(article.getLikeCount() + 1);
            articleMapper.updateById(article);
        }
    }

} 
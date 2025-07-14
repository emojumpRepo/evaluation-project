package cn.iocoder.yudao.module.member.service.baby;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyUpdateReqVO;
import cn.iocoder.yudao.module.member.convert.baby.MemberBabyConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.member.dal.mysql.baby.MemberBabyMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.member.enums.ErrorCodeConstants.BABY_NOT_EXISTS;

/**
 * 宝宝信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MemberBabyServiceImpl implements MemberBabyService {

    @Resource
    private MemberBabyMapper babyMapper;

    @Override
    public Long createBaby(AppMemberBabyCreateReqVO createReqVO) {
        // 插入
        MemberBabyDO baby = MemberBabyConvert.INSTANCE.convert(createReqVO);
        babyMapper.insert(baby);
        // 返回
        return baby.getId();
    }

    @Override
    public void updateBaby(AppMemberBabyUpdateReqVO updateReqVO) {
        // 校验存在
        validateBabyExists(updateReqVO.getId());
        // 更新
        MemberBabyDO updateObj = MemberBabyConvert.INSTANCE.convert(updateReqVO);
        babyMapper.updateById(updateObj);
    }

    @Override
    public void deleteBaby(Long id) {
        // 校验存在
        validateBabyExists(id);
        // 删除
        babyMapper.deleteById(id);
    }

    private void validateBabyExists(Long id) {
        if (babyMapper.selectById(id) == null) {
            throw exception(BABY_NOT_EXISTS);
        }
    }

    @Override
    public MemberBabyDO getBaby(Long id) {
        return babyMapper.selectById(id);
    }

    @Override
    public PageResult<MemberBabyDO> getBabyList(MemberBabyPageReqVO pageReqVO) {
        return babyMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MemberBabyDO> getBabyListByUserId(Long userId) {
        return babyMapper.selectListByUserId(userId);
    }

} 
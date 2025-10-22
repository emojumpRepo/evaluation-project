package cn.iocoder.yudao.module.member.service.baby;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyUpdateReqVO;
import cn.iocoder.yudao.module.member.convert.baby.MemberBabyConvert;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import cn.iocoder.yudao.module.member.dal.mysql.baby.MemberBabyMapper;
import cn.iocoder.yudao.module.member.dal.mysql.user.MemberUserMapper;
import cn.iocoder.yudao.module.member.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

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
    @Resource
    private MemberUserMapper memberUserMapper;

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

    public void validateBabyExists(Long id) {
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
        // 新增：如果传入手机号，先查用户ID
        if (StrUtil.isNotEmpty(pageReqVO.getMobile())) {
            Long userId = memberUserMapper.selectIdByMobile(pageReqVO.getMobile());
            if (userId == null) {
                // 返回空分页
                return new PageResult<>();
            }
            pageReqVO.setUserId(userId);
        }
        return babyMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MemberBabyDO> getBabyListByUserId(Long userId) {
        return babyMapper.selectListByUserId(userId);
    }

    @Override
    public boolean isBabyParent(Long userId, Long babyId) {
        if (userId == null || babyId == null) {
            return false;
        }
        List<MemberBabyDO> babyList = getBabyListByUserId(userId);
        return babyList.stream().anyMatch(baby -> baby.getId().equals(babyId));
    }

} 
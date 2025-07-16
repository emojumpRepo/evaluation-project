package cn.iocoder.yudao.module.system.service.platform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.system.controller.admin.platform.vo.policy.PolicyPageReqVO;
import cn.iocoder.yudao.module.system.controller.admin.platform.vo.policy.PolicyUpdateReqVO;
import cn.iocoder.yudao.module.system.convert.platform.PolicyConvert;
import cn.iocoder.yudao.module.system.dal.dataobject.platform.PolicyDO;
import cn.iocoder.yudao.module.system.dal.mysql.platform.PolicyMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.system.enums.ErrorCodeConstants.POLICY_NOT_EXISTS;

/**
 * 政策配置 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class PolicyServiceImpl implements PolicyService {

    @Resource
    private PolicyMapper policyMapper;

    @Override
    public void updatePolicy(@Valid PolicyUpdateReqVO updateReqVO) {
        // 校验存在
        validatePolicyExists(updateReqVO.getId());
        // 更新
        PolicyDO updateObj = PolicyConvert.INSTANCE.convert(updateReqVO);
        policyMapper.updateById(updateObj);
    }

    private void validatePolicyExists(Long id) {
        if (policyMapper.selectById(id) == null) {
            throw exception(POLICY_NOT_EXISTS);
        }
    }

    @Override
    public PolicyDO getPolicy(Long id) {
        return policyMapper.selectById(id);
    }

    @Override
    public List<PolicyDO> getPolicyList(Collection<Long> ids) {
        return policyMapper.selectByIds(ids);
    }

    @Override
    public PageResult<PolicyDO> getPolicyPage(PolicyPageReqVO pageReqVO) {
        return policyMapper.selectPage(pageReqVO);
    }

    @Override
    public PolicyDO getPolicyByType(String type) {
        return policyMapper.selectByType(type);
    }

}

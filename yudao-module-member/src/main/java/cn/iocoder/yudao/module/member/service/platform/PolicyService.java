package cn.iocoder.yudao.module.member.service.platform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.policy.PolicyPageReqVO;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.policy.PolicyUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.PolicyDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 政策配置 Service 接口
 *
 * @author 芋道源码
 */
public interface PolicyService {

    /**
     * 更新政策配置
     *
     * @param updateReqVO 更新信息
     */
    void updatePolicy(@Valid PolicyUpdateReqVO updateReqVO);

    /**
     * 获得政策配置
     *
     * @param id 编号
     * @return 政策配置
     */
    PolicyDO getPolicy(Long id);

    /**
     * 获得政策配置列表
     *
     * @param ids 编号
     * @return 政策配置列表
     */
    List<PolicyDO> getPolicyList(Collection<Long> ids);

    /**
     * 获得政策配置分页
     *
     * @param pageReqVO 分页查询
     * @return 政策配置分页
     */
    PageResult<PolicyDO> getPolicyPage(PolicyPageReqVO pageReqVO);

    /**
     * 根据类型获得政策配置
     *
     * @param type 政策类型
     * @return 政策配置
     */
    PolicyDO getPolicyByType(String type);

}

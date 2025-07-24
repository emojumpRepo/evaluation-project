package cn.iocoder.yudao.module.member.convert.platform;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.policy.PolicyRespVO;
import cn.iocoder.yudao.module.member.controller.admin.platform.vo.policy.PolicyUpdateReqVO;
import cn.iocoder.yudao.module.member.controller.app.platform.vo.AppPolicyRespVO;
import cn.iocoder.yudao.module.member.dal.dataobject.platform.PolicyDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 政策配置 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface PolicyConvert {

    PolicyConvert INSTANCE = Mappers.getMapper(PolicyConvert.class);

    PolicyDO convert(PolicyUpdateReqVO bean);

    PolicyRespVO convert(PolicyDO bean);

    List<PolicyRespVO> convertList(List<PolicyDO> list);

    PageResult<PolicyRespVO> convertPage(PageResult<PolicyDO> page);

    // ========== APP 相关 ==========

    AppPolicyRespVO convertApp(PolicyDO bean);

}

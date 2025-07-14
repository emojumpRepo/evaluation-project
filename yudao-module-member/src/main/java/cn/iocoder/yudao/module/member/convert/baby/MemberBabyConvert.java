package cn.iocoder.yudao.module.member.convert.baby;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyRespVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyRespVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 宝宝信息 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberBabyConvert {

    MemberBabyConvert INSTANCE = Mappers.getMapper(MemberBabyConvert.class);

    MemberBabyRespVO convert(MemberBabyDO bean);

    List<MemberBabyRespVO> convertList(List<MemberBabyDO> list);

    PageResult<MemberBabyRespVO> convertPage(PageResult<MemberBabyDO> page);

    // ========== App 端相关 ==========

    MemberBabyDO convert(AppMemberBabyCreateReqVO bean);

    MemberBabyDO convert(AppMemberBabyUpdateReqVO bean);

    AppMemberBabyRespVO convertApp(MemberBabyDO bean);

    List<AppMemberBabyRespVO> convertAppList(List<MemberBabyDO> list);

} 
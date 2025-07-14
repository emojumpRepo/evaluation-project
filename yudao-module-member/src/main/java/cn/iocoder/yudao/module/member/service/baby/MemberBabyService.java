package cn.iocoder.yudao.module.member.service.baby;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.member.controller.admin.baby.vo.MemberBabyPageReqVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyCreateReqVO;
import cn.iocoder.yudao.module.member.controller.app.baby.vo.AppMemberBabyUpdateReqVO;
import cn.iocoder.yudao.module.member.dal.dataobject.baby.MemberBabyDO;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

/**
 * 宝宝信息 Service 接口
 *
 * @author 芋道源码
 */
public interface MemberBabyService {

    /**
     * 创建宝宝信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createBaby(@Valid AppMemberBabyCreateReqVO createReqVO);

    /**
     * 更新宝宝信息
     *
     * @param updateReqVO 更新信息
     */
    void updateBaby(@Valid AppMemberBabyUpdateReqVO updateReqVO);

    /**
     * 删除宝宝信息
     *
     * @param id 编号
     */
    void deleteBaby(Long id);

    /**
     * 获得宝宝信息
     *
     * @param id 编号
     * @return 宝宝信息
     */
    MemberBabyDO getBaby(Long id);

    /**
     * 获得宝宝信息列表
     *
     * @param pageReqVO 分页查询
     * @return 宝宝信息列表
     */
    PageResult<MemberBabyDO> getBabyList(MemberBabyPageReqVO pageReqVO);

    /**
     * 根据用户编号获得宝宝信息列表
     *
     * @param userId 用户编号
     * @return 宝宝信息列表
     */
    List<MemberBabyDO> getBabyListByUserId(Long userId);

} 
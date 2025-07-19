package cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 问卷访问记录 DO
 *
 * @author 芋道源码
 */
@TableName("emo_questionnaire_access")
@KeySequence("emo_questionnaire_access_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireAccessDO extends TenantBaseDO {

    /**
     * 访问记录编号
     */
    @TableId
    private Long id;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 访问时间
     */
    private LocalDateTime accessTime;

}
package cn.iocoder.yudao.module.emojump.dal.dataobject.assessment;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 测评 DO
 *
 * @author 芋道源码
 */
@TableName("emo_assessment")
@KeySequence("emo_assessment_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentDO extends TenantBaseDO {

    /**
     * 测评编号
     */
    @TableId
    private Long id;

    /**
     * 测评标题
     */
    private String title;

    /**
     * 测评描述
     */
    private String description;

    /**
     * 测评类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.emojump.enums.AssessmentTypeEnum}
     */
    private Integer type;

    /**
     * 测评状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.emojump.enums.AssessmentStatusEnum}
     */
    private Integer status;

    /**
     * 目标人群
     */
    private String targetAudience;

    /**
     * 测评时长（分钟）
     */
    private Integer duration;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 是否需要预约
     */
    private Boolean needAppointment;

    /**
     * 是否可以重复测评
     */
    private Boolean isRepeatable;

    /**
     * 最大参与人数
     */
    private Integer maxParticipants;

    /**
     * 当前参与人数
     */
    private Integer currentParticipants;

    /**
     * 备注
     */
    private String remark;

}
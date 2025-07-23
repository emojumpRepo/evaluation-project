package cn.iocoder.yudao.module.emojump.dal.dataobject.assessment;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 测评结果 DO
 *
 * @author 芋道源码
 */
@TableName("emo_assessment_result")
@KeySequence("emo_assessment_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentResultDO extends TenantBaseDO {

    /**
     * 结果编号
     */
    @TableId
    private Long id;

    /**
     * 测评ID
     */
    private Long assessmentId;

    /**
     * 宝宝ID
     */
    private Long babyId;

    /**
     * 总体得分
     */
    private BigDecimal overallScore;

    /**
     * 总体评级
     */
    private String overallLevel;

    /**
     * 总体测评报告
     */
    private String overallReport;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

    /**
     * 状态：0-进行中 1-已完成
     */
    private Integer status;

}
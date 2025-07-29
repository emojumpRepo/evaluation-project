package cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷结果 DO
 *
 * @author 芋道源码
 */
@TableName("emo_questionnaire_result")
@KeySequence("emo_questionnaire_result_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireResultDO extends TenantBaseDO {

    /**
     * 问卷结果编号
     */
    @TableId
    private Long id;

    /**
     * 评估ID
     */
    private Long assessmentId;

    /**
     * 测评结果ID
     */
    private Long assessmentResultId;

    /**
     * 宝宝ID
     */
    private Long babyId;

    /**
     * 问卷ID
     */
    private Long questionnaireId;

    /**
     * 问卷结果数据（JSON格式）
     */
    private String resultData;

    /**
     * 用户填写的答案数据（JSON格式）
     */
    private String answerData;

    /**
     * 问卷得分
     */
    private BigDecimal score;

    /**
     * 问卷评级
     */
    private String level;

    /**
     * 问卷报告
     */
    private String report;

    /**
     * 完成时间
     */
    private LocalDateTime completedTime;

}
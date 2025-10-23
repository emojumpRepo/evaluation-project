package cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaireresult;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
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
@KeySequence("emo_questionnaire_result_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EmoQuestionnaireResultDO extends BaseDO {

    /**
     * 问卷结果编号
     */
    @TableId
    private Long id;
    
    /**
     * 测评结果ID
     */
    private Long assessmentResultId;

    /**
     * 测评ID
     */
    private Long assessmentId;

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

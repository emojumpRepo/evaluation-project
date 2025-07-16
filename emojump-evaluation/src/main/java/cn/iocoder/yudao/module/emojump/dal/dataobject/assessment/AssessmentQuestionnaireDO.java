package cn.iocoder.yudao.module.emojump.dal.dataobject.assessment;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;

/**
 * 测评问卷关联 DO
 *
 * @author 芋道源码
 */
@TableName("emo_assessment_questionnaire")
@KeySequence("emo_assessment_questionnaire_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentQuestionnaireDO extends BaseDO {

    /**
     * 关联编号
     */
    @TableId
    private Long id;
    
    /**
     * 测评ID
     */
    private Long assessmentId;
    
    /**
     * 问卷ID
     */
    private Long questionnaireId;
    
    /**
     * 排序顺序
     */
    private Integer sortOrder;
    
    /**
     * 是否必填
     */
    private Boolean isRequired;
    
    /**
     * 权重（用于计算总分）
     */
    private BigDecimal weight;

}

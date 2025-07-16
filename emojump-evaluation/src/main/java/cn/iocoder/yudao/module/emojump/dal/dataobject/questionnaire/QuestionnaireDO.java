package cn.iocoder.yudao.module.emojump.dal.dataobject.questionnaire;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 问卷 DO
 *
 * @author 芋道源码
 */
@TableName("emo_questionnaire")
@KeySequence("emo_questionnaire_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireDO extends BaseDO {

    /**
     * 问卷编号
     */
    @TableId
    private Long id;
    
    /**
     * 问卷标题
     */
    private String title;
    
    /**
     * 问卷描述
     */
    private String description;
    
    /**
     * 问卷链接
     */
    private String link;
    
    /**
     * 问卷类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.emojump.enums.QuestionnaireTypeEnum}
     */
    private Integer type;
    
    /**
     * 问卷状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.emojump.enums.QuestionnaireStatusEnum}
     */
    private Integer status;
    
    /**
     * 目标人群
     */
    private String targetAudience;
    
    /**
     * 预计时长（分钟）
     */
    private Integer estimatedDuration;
    
    /**
     * 访问次数
     */
    private Integer accessCount;
    
    /**
     * 完成次数
     */
    private Integer completionCount;
    
    /**
     * 是否开放
     */
    private Boolean isOpen;
    
    /**
     * 有效期开始时间
     */
    private LocalDateTime validFrom;
    
    /**
     * 有效期结束时间
     */
    private LocalDateTime validTo;
    
    /**
     * 备注
     */
    private String remark;

} 

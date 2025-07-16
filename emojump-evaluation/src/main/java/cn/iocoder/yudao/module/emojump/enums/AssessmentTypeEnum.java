package cn.iocoder.yudao.module.emojump.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测评类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum AssessmentTypeEnum {

    CHILD_DEVELOPMENT(1, "儿童发展测评"),
    BEHAVIOR_EVALUATION(2, "行为评估"),
    COGNITIVE_ASSESSMENT(3, "认知能力测评"),
    EMOTIONAL_ASSESSMENT(4, "情感发展测评"),
    SOCIAL_SKILLS(5, "社交技能测评");

    /**
     * 类型值
     */
    private final Integer type;
    
    /**
     * 类型名称
     */
    private final String name;

} 

package cn.iocoder.yudao.module.emojump.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问卷类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum QuestionnaireTypeEnum {

    CHILD_DEVELOPMENT(1, "儿童发展问卷"),
    BEHAVIOR_SURVEY(2, "行为调查问卷"),
    COGNITIVE_TEST(3, "认知测试问卷"),
    EMOTIONAL_SURVEY(4, "情感调查问卷"),
    SOCIAL_ASSESSMENT(5, "社交评估问卷"),
    PARENT_FEEDBACK(6, "家长反馈问卷"),
    TEACHER_EVALUATION(7, "教师评估问卷");

    /**
     * 类型值
     */
    private final Integer type;
    
    /**
     * 类型名称
     */
    private final String name;

} 

package cn.iocoder.yudao.module.emojump.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 问卷状态枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum QuestionnaireStatusEnum {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    OFFLINE(2, "已下线"),
    INVALID(3, "已失效");

    /**
     * 状态值
     */
    private final Integer status;
    
    /**
     * 状态名称
     */
    private final String name;

} 

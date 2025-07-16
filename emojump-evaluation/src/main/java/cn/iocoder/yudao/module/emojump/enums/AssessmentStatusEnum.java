package cn.iocoder.yudao.module.emojump.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 测评状态枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum AssessmentStatusEnum {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    ENDED(2, "已结束"),
    CANCELLED(3, "已取消");

    /**
     * 状态值
     */
    private final Integer status;
    
    /**
     * 状态名称
     */
    private final String name;

} 

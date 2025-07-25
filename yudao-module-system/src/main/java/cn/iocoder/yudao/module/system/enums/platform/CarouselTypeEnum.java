package cn.iocoder.yudao.module.system.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 轮播图类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum CarouselTypeEnum {

    REDIRECT(1, "跳转链接"),
    POPUP(2, "弹窗");

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 描述
     */
    private final String description;
}

package cn.iocoder.yudao.module.system.enums.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 政策类型枚举
 *
 * @author 芋道源码
 */
@AllArgsConstructor
@Getter
public enum PolicyTypeEnum {

    SERVICE_AGREEMENT("service_agreement", "服务协议"),
    PRIVACY_POLICY("privacy_policy", "隐私政策");

    /**
     * 类型
     */
    private final String type;
    /**
     * 名称
     */
    private final String name;

}

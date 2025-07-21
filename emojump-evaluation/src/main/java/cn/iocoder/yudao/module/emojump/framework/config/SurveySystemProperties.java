package cn.iocoder.yudao.module.emojump.framework.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 外部问卷系统配置属性
 *
 * @author 芋道源码
 */
@Data
@Component
@ConfigurationProperties(prefix = "yudao.survey-system")
public class SurveySystemProperties {

    /**
     * 是否启用外部问卷系统同步
     */
    private Boolean enabled = true;

    /**
     * 外部问卷系统基础URL
     */
    private String baseUrl = "http://localhost:8080";

    /**
     * 获取问卷列表的API路径
     */
    private String surveyListPath = "/api/survey/getSurveyList";

    /**
     * 连接超时时间（毫秒）
     */
    private Integer connectTimeout = 5000;

    /**
     * 读取超时时间（毫秒）
     */
    private Integer readTimeout = 10000;

    /**
     * 重试次数
     */
    private Integer retryCount = 3;

    /**
     * API密钥（如果需要认证）
     */
    private String apiKey;

    /**
     * 获取完整的问卷列表URL
     */
    public String getSurveyListUrl() {
        return baseUrl + surveyListPath;
    }

}

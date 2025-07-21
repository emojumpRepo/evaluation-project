package cn.iocoder.yudao.module.emojump.framework.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * 外部问卷系统配置类
 *
 * @author 芋道源码
 */
@Configuration
public class SurveySystemConfig {

    @Resource
    private SurveySystemProperties surveySystemProperties;

    /**
     * 创建用于外部问卷系统的RestTemplate
     * 如果项目中已有RestTemplate Bean，这个方法可以删除
     */
    @Bean("surveySystemRestTemplate")
    public RestTemplate surveySystemRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(surveySystemProperties.getConnectTimeout()))
                .setReadTimeout(Duration.ofMillis(surveySystemProperties.getReadTimeout()))
                .build();
    }

}

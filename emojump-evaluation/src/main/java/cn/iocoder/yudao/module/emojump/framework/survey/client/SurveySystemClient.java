package cn.iocoder.yudao.module.emojump.framework.survey.client;

import cn.iocoder.yudao.module.emojump.framework.config.SurveySystemProperties;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalSurveyApiResponse;
import cn.iocoder.yudao.module.emojump.framework.survey.vo.ExternalSurveyRespVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * 外部问卷系统客户端
 *
 * @author 芋道源码
 */
@Slf4j
@Component
public class SurveySystemClient {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private SurveySystemProperties surveySystemProperties;

    /**
     * 获取外部问卷系统的问卷列表
     *
     * @return 问卷列表
     */
    public List<ExternalSurveyRespVO> getSurveyList() {
        if (!surveySystemProperties.getEnabled()) {
            log.info("[getSurveyList] 外部问卷系统同步已禁用");
            return Collections.emptyList();
        }

        String url = surveySystemProperties.getSurveyListUrl();
        log.info("[getSurveyList] 开始请求外部问卷系统，URL: {}", url);

        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 如果配置了API密钥，添加到请求头
            if (StringUtils.hasText(surveySystemProperties.getSurveyAdminToken())) {
                headers.set("Authorization", "Bearer " + surveySystemProperties.getSurveyAdminToken());
            }

            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 发送请求
            ResponseEntity<ExternalSurveyApiResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ExternalSurveyApiResponse.class
            );

            // 处理响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                ExternalSurveyApiResponse apiResponse = response.getBody();
                
                if (apiResponse.isSuccess()) {
                    List<ExternalSurveyRespVO> surveys = apiResponse.getData();
                    log.info("[getSurveyList] 成功获取外部问卷列表，数量: {}", surveys != null ? surveys.size() : 0);
                    return surveys != null ? surveys : Collections.emptyList();
                } else {
                    log.error("[getSurveyList] 外部问卷系统返回错误，code: {}, message: {}", 
                            apiResponse.getCode(), apiResponse.getMessage());
                    return Collections.emptyList();
                }
            } else {
                log.error("[getSurveyList] 外部问卷系统响应异常，状态码: {}", response.getStatusCode());
                return Collections.emptyList();
            }

        } catch (RestClientException e) {
            log.error("[getSurveyList] 请求外部问卷系统失败，URL: {}, 错误: {}", url, e.getMessage(), e);
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("[getSurveyList] 处理外部问卷系统响应时发生异常", e);
            return Collections.emptyList();
        }
    }

    /**
     * 带重试机制的获取问卷列表
     *
     * @return 问卷列表
     */
    public List<ExternalSurveyRespVO> getSurveyListWithRetry() {
        int retryCount = surveySystemProperties.getRetryCount();
        
        for (int i = 0; i <= retryCount; i++) {
            try {
                List<ExternalSurveyRespVO> result = getSurveyList();
                if (!result.isEmpty()) {
                    return result;
                }
                
                if (i < retryCount) {
                    log.warn("[getSurveyListWithRetry] 第{}次请求返回空结果，准备重试", i + 1);
                    Thread.sleep(1000 * (i + 1)); // 递增延迟
                }
            } catch (Exception e) {
                if (i < retryCount) {
                    log.warn("[getSurveyListWithRetry] 第{}次请求失败，准备重试，错误: {}", i + 1, e.getMessage());
                    try {
                        Thread.sleep(1000 * (i + 1)); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("[getSurveyListWithRetry] 重试{}次后仍然失败", retryCount, e);
                }
            }
        }
        
        return Collections.emptyList();
    }

}

package cn.iocoder.yudao.module.emojump.controller.admin.questionnaire;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.emojump.controller.admin.vo.questionnaire.QuestionnairePageReqVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 问卷调试控制器
 *
 * @author 芋道源码
 */
@Tag(name = "管理后台 - 问卷调试")
@RestController
@RequestMapping("/emojump/questionnaire-debug")
@Validated
@Slf4j
public class QuestionnaireDebugController {

    @GetMapping("/test-params")
    @Operation(summary = "测试参数解析")
    public CommonResult<Map<String, Object>> testParams(
            HttpServletRequest request,
            @Valid QuestionnairePageReqVO pageVO) {
        
        log.info("[testParams] 开始测试参数解析");
        
        Map<String, Object> result = new HashMap<>();
        
        // 1. 记录原始请求参数
        Map<String, String> rawParams = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            rawParams.put(paramName, paramValue);
            log.info("[testParams] 原始参数: {}={}", paramName, paramValue);
        }
        result.put("rawParams", rawParams);
        
        // 2. 记录解析后的VO对象
        Map<String, Object> parsedVO = new HashMap<>();
        parsedVO.put("pageNo", pageVO.getPageNo());
        parsedVO.put("pageSize", pageVO.getPageSize());
        parsedVO.put("title", pageVO.getTitle());
        parsedVO.put("status", pageVO.getStatus());
        parsedVO.put("type", pageVO.getType());
        parsedVO.put("isOpen", pageVO.getIsOpen());
        parsedVO.put("createTime", pageVO.getCreateTime());
        result.put("parsedVO", parsedVO);
        
        // 3. 记录请求信息
        Map<String, String> requestInfo = new HashMap<>();
        requestInfo.put("method", request.getMethod());
        requestInfo.put("uri", request.getRequestURI());
        requestInfo.put("queryString", request.getQueryString());
        requestInfo.put("contentType", request.getContentType());
        result.put("requestInfo", requestInfo);
        
        log.info("[testParams] 参数解析成功: {}", result);
        
        return success(result);
    }

    @GetMapping("/test-simple")
    @Operation(summary = "简单参数测试")
    public CommonResult<Map<String, Object>> testSimple(
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "type", required = false) Integer type) {
        
        log.info("[testSimple] 简单参数测试: pageNo={}, pageSize={}, status={}, type={}", 
                pageNo, pageSize, status, type);
        
        Map<String, Object> result = new HashMap<>();
        result.put("pageNo", pageNo);
        result.put("pageSize", pageSize);
        result.put("status", status);
        result.put("type", type);
        
        return success(result);
    }

}

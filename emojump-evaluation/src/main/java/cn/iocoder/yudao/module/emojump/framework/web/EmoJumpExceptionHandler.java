package cn.iocoder.yudao.module.emojump.framework.web;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * EmoJump 模块异常处理器
 *
 * @author 芋道源码
 */
@RestControllerAdvice(basePackages = "cn.iocoder.yudao.module.emojump")
@Slf4j
@Order(1) // 优先级高于全局异常处理器
@Component("emoJumpExceptionHandler") // 指定Bean名称避免冲突
public class EmoJumpExceptionHandler {

    /**
     * 处理方法参数类型不匹配异常
     * 例如：将字符串 "$D" 转换为 Integer 时失败
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonResult<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn("[handleMethodArgumentTypeMismatchException] 参数类型转换失败: 参数名={}, 参数值={}, 目标类型={}, 错误信息={}", 
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName(), ex.getMessage());
        
        String message = String.format("参数 '%s' 的值 '%s' 无法转换为 %s 类型，请检查参数格式", 
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName());
        
        return CommonResult.error(400, message);
    }

    /**
     * 处理HTTP消息不可读异常
     * 例如：JSON格式错误或数据绑定失败
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public CommonResult<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("[handleHttpMessageNotReadableException] HTTP消息读取失败: {}", ex.getMessage());
        
        String message = "请求参数格式错误，请检查参数类型和格式";
        if (ex.getMessage().contains("Cannot deserialize")) {
            message = "JSON数据格式错误，请检查请求体格式";
        } else if (ex.getMessage().contains("NumberFormatException")) {
            message = "数字格式错误，请检查数字参数的格式";
        }
        
        return CommonResult.error(400, message);
    }

    /**
     * 处理数字格式异常
     */
    @ExceptionHandler(NumberFormatException.class)
    public CommonResult<?> handleNumberFormatException(NumberFormatException ex) {
        log.warn("[handleNumberFormatException] 数字格式转换失败: {}", ex.getMessage());
        
        String message = "数字格式错误，请检查数字参数的格式";
        if (ex.getMessage().contains("$D")) {
            message = "检测到特殊字符 '$D'，请确保传递正确的数字参数";
        }
        
        return CommonResult.error(400, message);
    }

    /**
     * 处理通用的运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public CommonResult<?> handleRuntimeException(RuntimeException ex) {
        // 检查是否是数据绑定相关的异常
        if (ex.getCause() instanceof NumberFormatException) {
            return handleNumberFormatException((NumberFormatException) ex.getCause());
        }
        
        log.error("[handleRuntimeException] 运行时异常: ", ex);
        return CommonResult.error(500, "系统内部错误: " + ex.getMessage());
    }

}

package com.huike.video.common.exception;

import cn.dev33.satoken.exception.NotLoginException;
import com.huike.video.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<String> handleNoResourceFoundException(NoResourceFoundException e) {
        log.debug("No static resource: {}", e.getResourcePath());
        return Result.error(99999, "资源不存在");
    }

    @ExceptionHandler(NotLoginException.class)
    public Result<String> handleNotLoginException(NotLoginException nle) {
        log.warn("Not Login: {}", nle.getMessage());
        return Result.error(10001, "用户未登录");
    }

    @ExceptionHandler(BusinessException.class)
    public Result<String> handleBusinessException(BusinessException e) {
        log.warn("Business Exception: {}", e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public Result<String> handleValidException(Exception e) {
        String message = "参数错误";
        if (e instanceof MethodArgumentNotValidException manve && manve.getBindingResult().getFieldError() != null) {
            message = manve.getBindingResult().getFieldError().getDefaultMessage();
        } else if (e instanceof BindException be && be.getBindingResult().getFieldError() != null) {
            message = be.getBindingResult().getFieldError().getDefaultMessage();
        }
        return Result.error(99999, message);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("Runtime Exception", e);
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("System Exception", e);
        return Result.error("System Error");
    }
}

package com.tee.controller.exception;

import com.tee.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AppException.class)
    @ResponseBody
    public ResponseEntity<Object> handleCustomException(AppException ex) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.error("AppException", ex);
        // 可以根据需要构建响应体
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", String.valueOf(LocalDateTime.now()));
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "请求异常");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Object> handleCustomException(Exception ex) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.error("Exception", ex);
        // 可以根据需要构建响应体
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", String.valueOf(LocalDateTime.now()));
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "系统异常");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

}

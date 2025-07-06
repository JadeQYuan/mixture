package com.tee.controller.exception;

import com.tee.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class FilterExceptionCollection {
    @RequestMapping("/service/error")
    public ResponseEntity<Object> handleError(HttpServletRequest request) {
        Exception exception = (Exception) request.getAttribute("exception");
        if (exception instanceof AppException) {
            // 可以根据需要构建响应体
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", String.valueOf(LocalDateTime.now()));
            Object code = request.getAttribute("code");
            int value = HttpStatus.INTERNAL_SERVER_ERROR.value();
            if (!Objects.isNull(code)) {
                value = HttpStatus.UNAUTHORIZED.value();
            }
            body.put("status", value);
            body.put("error", "请求异常");
            body.put("message", exception.getMessage());
            body.put("path", request.getRequestURI()); // 你可以在这里加入请求的路径等信息

            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        } else {
            // 可以根据需要构建响应体
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("timestamp", String.valueOf(LocalDateTime.now()));
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("error", "系统异常");
            body.put("message", exception.getMessage());
            body.put("path", request.getRequestURI()); // 你可以在这里加入请求的路径等信息

            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}

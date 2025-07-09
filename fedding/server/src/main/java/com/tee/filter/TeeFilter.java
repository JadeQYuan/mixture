package com.tee.filter;

import com.tee.constant.Contants;
import com.tee.exception.AppException;
import com.tee.pojo.vo.User;
import com.tee.service.UserService;
import com.tee.util.JwtUtils;
import com.tee.util.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
public class TeeFilter implements Filter {

    private String[] excludedUris;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String param = filterConfig.getInitParameter("excludedUris");
        if (!StringUtils.isEmpty(param)) {
            this.excludedUris = param.split(",");
        }

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取请求地址
        String requestUrl = request.getRequestURI();
        log.info("请求地址为: {}", requestUrl);
        boolean flag = false;
        for (String uri : excludedUris) {
            if (requestUrl.equals(uri)){
                flag = true;
                break;
            }
        }

        try {
            if (!flag) {
                String authorization = request.getHeader(Contants.AUTHORIZATION);
                String userId = null;
                try {
                    userId = JwtUtils.getSubject(authorization);
                } catch (Exception e) {
                    log.error("getSubject failed", e);
                    throw new AppException("authToken 校验失败！");
                }
                // 查库 校验userId是否存在即可
                UserService userService = SpringApplicationContext.getBean(UserService.class);
                List<User> userInfo = userService.getUserInfo(userId);

                if (CollectionUtils.isEmpty(userInfo)) {
                    log.error("userInfo is null");
                    throw new AppException("authToken 校验失败, 用户不存在");
                }
            } else {
                log.info("cookie校验 跳过url {}", requestUrl);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.error("doFilter failed.", e);
            request.setAttribute("exception", e);
            request.setAttribute("code", "401");
            request.getRequestDispatcher("/service/error").forward(request, response);
        }
    }
}

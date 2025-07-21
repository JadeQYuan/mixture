package cn.domain.filter;

import cn.domain.constant.Constants;
import cn.domain.exception.AppException;
import cn.domain.util.TokenUtil;

import org.springframework.util.StringUtils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthFilter implements Filter {

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
                String authorization = request.getHeader(Constants.AUTHORIZATION);
                try {
                    String userId = TokenUtil.getSubject(authorization);
                    TokenUtil.setToken(Integer.parseInt(userId));
                } catch (Exception e) {
                    log.error("getSubject failed", e);
                    throw new AppException("authToken 校验失败！");
                }
            } else {
                log.info("cookie校验 跳过url {}", requestUrl);
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            log.error("doFilter failed.", e);
            request.setAttribute("exception", e);
            request.setAttribute("code", "401");
            request.getRequestDispatcher("/api/error").forward(request, response);
        }
    }
}

package com.tee.config;

import com.tee.filter.TeeFilter;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.sqlite.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Bean
    public FilterRegistrationBean<TeeFilter> cookieFilter() {
        FilterRegistrationBean<TeeFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TeeFilter());
        registrationBean.addUrlPatterns("/*"); // 拦截所有请求路径
        registrationBean.setOrder(0);// 优先级，越低越优先
        List<String> excludedUris = new ArrayList<>();
        excludedUris.add("/service/login/account");
        excludedUris.add("/service/login/face");
        registrationBean.addInitParameter("excludedUris", StringUtils.join(excludedUris, ",")); // 需要排除的uri
        return registrationBean;
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}

package cn.domain.config;

import cn.domain.filter.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.sqlite.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

    @Bean
    public FilterRegistrationBean<AuthFilter> cookieFilter() {
        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthFilter());
        registrationBean.addUrlPatterns("/api/*"); // 拦截所有请求路径
        registrationBean.setOrder(0);// 优先级，越低越优先
        List<String> excludedUris = new ArrayList<>();
        excludedUris.add("/api/login/account");
        excludedUris.add("/api/login/face");
        excludedUris.add("/api/mixture/todo");
        registrationBean.addInitParameter("excludedUris", StringUtils.join(excludedUris, ",")); // 需要排除的uri
        return registrationBean;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowCredentials(true);
    }
}

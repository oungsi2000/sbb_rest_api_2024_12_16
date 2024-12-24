package com.ll.jumptospringboot.config;

import com.ll.jumptospringboot.domain.oauth2.RegistrationChecker;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.concurrent.TimeUnit;

@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/templates/", "classpath:/static/")
            .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RegistrationChecker())
            .addPathPatterns("/**") // 인터셉터를 적용할 URL 패턴
            .excludePathPatterns("/oauth-signup/**", "/api/signup/oauth/**", "/login/**", "/**/*.css", "/error"); // 인터셉터에서 제외할 URL 패턴
    }
}

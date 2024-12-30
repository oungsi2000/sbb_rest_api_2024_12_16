package com.ll.jumptospringboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000") // 허용할 출처
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
            .allowedHeaders("*") // 허용할 헤더
            .exposedHeaders("Authorization", "x-csrf-token", "Content-Type") // 필요한 헤더들을 나열
            .allowCredentials(true); // 쿠키 전달 허용 (필요한 경우)
    }
}

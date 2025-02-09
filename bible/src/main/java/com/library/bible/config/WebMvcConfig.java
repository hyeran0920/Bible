package com.library.bible.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.library.bible.commons.HttpMethodOverrideFilter;
import com.library.bible.resolver.AuthMemberArgumentResolver;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthMemberArgumentResolver authMemberArgumentResolver;
    private final CorsConfiguration corsConfiguration;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authMemberArgumentResolver);
    }

    @Bean
    FilterRegistrationBean<HttpMethodOverrideFilter> httpMethodOverrideFilter() {
        FilterRegistrationBean<HttpMethodOverrideFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpMethodOverrideFilter());
        registrationBean.addUrlPatterns("/*"); // 필터가 적용될 URL 패턴
        return registrationBean;
    }

    @Bean
    Filter shallowEtagHeaderFilter() {
    	return new ShallowEtagHeaderFilter();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(corsConfiguration.getAllowedOrigins().toArray(String[]::new))
            .allowedMethods(corsConfiguration.getAllowedMethods().toArray(String[]::new))
            .allowedHeaders(corsConfiguration.getAllowedHeaders().toArray(String[]::new))
            .allowCredentials(corsConfiguration.getAllowCredentials())
            .exposedHeaders(corsConfiguration.getExposedHeaders().toArray(String[]::new)); // 설정된 exposedHeaders 사용
    }
    
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/")
                .setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES));
    }

}
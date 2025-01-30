package com.library.bible.config;

import java.util.List;
import java.util.Locale;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

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
            .allowCredentials(corsConfiguration.getAllowCredentials());
    }
    //i18n
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setDefaultLocale(Locale.KOREAN); // 기본 언어 한국어 설정
        return localeResolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang"); // URL에서 `?lang=en`으로 언어 변경 가능
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
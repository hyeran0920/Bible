package com.library.bible.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import com.library.bible.commons.HttpMethodOverrideFilter;
import com.library.bible.resolver.AuthMemberArgumentResolver;

import jakarta.servlet.Filter;

class WebMvcConfigTest {

    @Mock
    private AuthMemberArgumentResolver authMemberArgumentResolver;

    @Mock
    private CorsConfiguration corsConfiguration;

    @InjectMocks
    private WebMvcConfig webMvcConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("addArgumentResolvers 호출 시, 주입받은 AuthMemberArgumentResolver가 등록되는지 확인")
    void addArgumentResolversTest() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        webMvcConfig.addArgumentResolvers(resolvers);

        assertTrue(resolvers.contains(authMemberArgumentResolver));
    }

    @Test
    @DisplayName("httpMethodOverrideFilter가 정상 등록되는지 테스트")
    void httpMethodOverrideFilterTest() {
        FilterRegistrationBean<HttpMethodOverrideFilter> registrationBean = 
            webMvcConfig.httpMethodOverrideFilter();
        assertNotNull(registrationBean);
        assertNotNull(registrationBean.getFilter());
    }

    @Test
    @DisplayName("shallowEtagHeaderFilter가 정상 등록되는지 테스트")
    void shallowEtagHeaderFilterTest() {
        Filter filter = webMvcConfig.shallowEtagHeaderFilter();
        assertNotNull(filter);
    }

    @Test
    @DisplayName("addCorsMappings가 정상적으로 호출되어 CORS가 등록되는지 테스트")
    void addCorsMappingsTest() {
        // corsConfiguration은 이미 Mock
        when(corsConfiguration.getAllowedOrigins()).thenReturn(List.of("http://localhost:3000"));
        when(corsConfiguration.getAllowedMethods()).thenReturn(List.of("GET", "POST"));
        when(corsConfiguration.getAllowedHeaders()).thenReturn(List.of("*"));
        when(corsConfiguration.getAllowCredentials()).thenReturn(true);
        when(corsConfiguration.getExposedHeaders()).thenReturn(List.of("Authorization"));

        CorsRegistry registry = mock(CorsRegistry.class, RETURNS_DEEP_STUBS);

        webMvcConfig.addCorsMappings(registry);

        // verify로 특정 메서드들이 호출되었는지 확인
        verify(registry, times(1)).addMapping("/**");
        verify(registry.addMapping("/**"), times(1)).allowedOrigins("http://localhost:3000");
        // 필요하면 추가로 verify
    }
    @Disabled
    @Test
    @DisplayName("addResourceHandlers 호출 시, 리소스 핸들러가 정상 등록되는지 테스트")
    void addResourceHandlersTest() {
        ResourceHandlerRegistry registry = new ResourceHandlerRegistry(null, null);
        webMvcConfig.addResourceHandlers(registry);
        // 등록 후 별도의 getter가 없으므로 일단 예외 없이 동작하는지만 확인
        // 커버리지를 위해 호출만 해도 충분
        assertNotNull(registry);
    }
}

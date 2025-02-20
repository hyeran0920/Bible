package com.library.bible.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;

class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
    }

    @Test
    @DisplayName("Bean corsConfiguration()이 정상적으로 CorsConfiguration 객체를 반환한다.")
    void corsConfigurationTest() {
        CorsConfiguration config = corsConfig.corsConfiguration();
        assertNotNull(config);
        // 필요한 경우, config.getAllowedOrigins() 등이 잘 설정되었는지 추가로 검증
    }
}

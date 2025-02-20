package com.library.bible.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.swagger.v3.oas.models.OpenAPI;

class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    @DisplayName("openAPI()가 OpenAPI 객체를 정상적으로 반환하는지 테스트")
    void openAPITest() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
    }
}


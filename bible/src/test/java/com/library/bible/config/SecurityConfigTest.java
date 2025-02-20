package com.library.bible.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.library.bible.security.handler.CustomAccessDeniedHandler;
import com.library.bible.security.handler.CustomAuthenticationEntryPoint;
import com.library.bible.security.handler.CustomLogoutSuccessHandler;
import com.library.bible.security.jwt.JwtProvider;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityConfig securityConfig;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;
    @Mock
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
    @Mock
    private CustomAccessDeniedHandler accessDeniedHandler;
    @Mock
    private CustomLogoutSuccessHandler logoutSuccessHandler;
    @Mock
    private JwtProvider jwtProvider;
    @Mock
    private CorsConfiguration corsConfiguration;

    @InjectMocks
    private SecurityConfig securityConfigMock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("BCryptPasswordEncoder 빈이 정상 생성되는지 테스트")
    void passwordEncoderTest() {
        BCryptPasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
    }

    @Test
    @DisplayName("AuthenticationManager 빈이 정상 생성되는지 테스트")
    void authenticationManagerTest() throws Exception {
        AuthenticationManager manager = securityConfig.authenticationManager();
        assertNotNull(manager);
    }
    @Disabled
    @Test
    @DisplayName("SecurityFilterChain이 정상적으로 작동하는지 테스트")
    void securityFilterChainTest() throws Exception {
        // 허용된 엔드포인트 테스트
        mockMvc.perform(get("/api/members/user"))
                .andExpect(status().isOk());

        // 인증 없이 접근 불가능한 엔드포인트 테스트
        mockMvc.perform(post("/api/members/admin"))
                .andExpect(status().isForbidden());

        // 인증 없이 접근 가능해야 하는 엔드포인트 테스트
        mockMvc.perform(get("/api/members/token"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("CORS 설정이 정상적으로 적용되는지 테스트")
    void corsConfigurationSourceTest() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        assertNotNull(source);
    }
}

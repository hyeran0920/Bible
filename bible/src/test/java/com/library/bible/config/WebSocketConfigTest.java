package com.library.bible.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.library.bible.alarm.WebSocketHandler;
import com.library.bible.security.jwt.JwtProvider;

class WebSocketConfigTest {

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private WebSocketConfig webSocketConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("webSocketHandler()가 WebSocketHandler 객체를 반환하는지 테스트")
    void webSocketHandlerTest() {
        WebSocketHandler handler = webSocketConfig.webSocketHandler();
        assertNotNull(handler);
    }
    @Disabled
    @Test
    @DisplayName("registerWebSocketHandlers()가 정상적으로 WebSocketHandler를 등록하는지 테스트")
    void registerWebSocketHandlersTest() {
        WebSocketHandlerRegistry registry = mock(WebSocketHandlerRegistry.class);
        webSocketConfig.registerWebSocketHandlers(registry);

        verify(registry, times(1)).addHandler(any(WebSocketHandler.class), eq("/websocket"));
    }
}

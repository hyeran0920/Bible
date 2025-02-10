package com.library.bible.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.library.bible.alarm.WebSocketHandler;
import com.library.bible.security.jwt.JwtProvider;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private final JwtProvider jwtProvider;
	
	public WebSocketConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }
	
	@Bean
    public WebSocketHandler webSocketHandler() {
        return new WebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler(), "/websocket")
                .setAllowedOrigins("*");
    }
}

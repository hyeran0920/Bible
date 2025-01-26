package com.library.bible.security.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.library.bible.security.jwt.JwtProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	private final JwtProvider jwtProvider;
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request, 
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
    	jwtProvider.removeTokenInCookie(response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}

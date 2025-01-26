package com.library.bible.security.handler;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// 인증된 사용자가 권한이 없는 리소스에 접근할 때 호출됨
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		log.warn("Forbidden error happened: {}", accessDeniedException.getMessage());
		throw new CustomException(ExceptionCode.FORBIDDEN);		
	}
}

package com.library.bible.security.handler;

import java.io.IOException;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;

import ch.qos.logback.core.spi.ErrorCodes;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

// 로그인 실패 시 호출되는 핸들러
@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler{@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
	    log.warn("# Authentication failed: " + exception.getMessage());
	    
	    ExceptionCode exceptionCode;
	    if (exception instanceof BadCredentialsException) {
	        exceptionCode = ExceptionCode.INVALID_CREDENTIALS;
	    } else if (exception instanceof DisabledException) {
	        exceptionCode = ExceptionCode.ACCOUNT_DISABLED;
	    } else if (exception instanceof LockedException) {
	        exceptionCode = ExceptionCode.ACCOUNT_LOCKED;
	    } else if (exception instanceof AccountExpiredException) {
	        exceptionCode = ExceptionCode.ACCOUNT_EXPIRED;
	    } else if (exception instanceof CredentialsExpiredException) {
	        exceptionCode = ExceptionCode.CREDENTIALS_EXPIRED;
	    } else {
	        exceptionCode = ExceptionCode.INVALID_CREDENTIALS;
	    }
		throw new CustomException(exceptionCode);

	}
}

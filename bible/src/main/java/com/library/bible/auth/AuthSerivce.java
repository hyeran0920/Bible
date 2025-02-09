package com.library.bible.auth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.security.jwt.JwtProperties;
import com.library.bible.security.jwt.JwtProvider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthSerivce {
	private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
	
	// refresh token을 이용한 토큰 재발급
	public void validAndRegenerateToken(HttpServletRequest request, HttpServletResponse response) {
	    // 쿠키에서 refresh token 추출
	    String refreshToken = jwtProvider.resolveTokenInCookie(request, JwtProperties.REFRESH_COOKIE_STRING);
	    if (refreshToken == null) {
	        throw new CustomException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND);
	    }
        // JWT 토큰 prefix 제거
	    refreshToken = refreshToken.replace(JwtProperties.TOKEN_PREFIX, "");
	    
	    // Redis에서 Refresh Token 확인
        String redisValue = redisTemplate.opsForValue().get(refreshToken);
	    if (redisValue == null) {
	        throw new CustomException(ExceptionCode.REFRESH_TOKEN_NOT_FOUND);
	    }
	    String memId = redisValue.split(",")[0];
	    List<String> temp = Arrays.stream(redisValue
	    		.replace("[", "").replace("]", "").split(", "))
	    		.toList();
	    List<String> roles = new ArrayList<>();
	    roles.add(temp.get(1));
	    if(temp.size() > 2) roles.add(temp.get(2));

	    // Refresh Token 유효성 검증
	    try {
	    	jwtProvider.getClaimsAndVerifyRefreshToken(refreshToken);
	    } catch (Exception e) {
	        throw new CustomException(ExceptionCode.INVALID_TOKEN_SIGNATURE);
		}

		// 토큰들 생성 및 저장
		jwtProvider.generateAndSave(response, memId, roles);
	}
}

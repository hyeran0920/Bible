package com.library.bible.security.jwt;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final RedisTemplate<String, String> redisTemplate;

	// JWT 생성(access token)
	public String generateAccessToken(String memId, List<String> roles) {
        // HS256(RSA 방식 X)
        String jwt = JWT.create()
                .withSubject(memId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("roles", roles)
                .sign(Algorithm.HMAC256(JwtProperties.ACCESS_SECRET));
        return jwt;
	}
	
	// refresh token 생성
	public String generateRefreshToken(String memId) {
        // HS256(RSA 방식 X)
        String jwt = JWT.create()
                .withSubject(memId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.REFRESH_EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(JwtProperties.REFRESH_SECRET));
        return jwt;
	}
	
	// header에서 JWT 존재 확인
	public String resolveTokenInHeader(HttpServletRequest request, String headerString) {
		String accessToken = request.getHeader(headerString);
		return accessToken;
	}
	
	// cookie에서 JWT 존재 확인
	public String resolveTokenInCookie(HttpServletRequest request, String cookieString) {
      Cookie[] cookies = request.getCookies();
      
      if (cookies == null) return null;
      
      String accessToken = Arrays.stream(cookies)
    		    .filter(cookie -> cookieString.equals(cookie.getName()))
    		    .findFirst()
    		    .map(Cookie::getValue)
    		    .orElse(null);
      return accessToken;
	}
	
//	// 토큰 검증 후 memId 반환
//	public Long getMemIdAndVerifyAccessToken(String accessToken) {
//        // JWT 토큰을 검증해서 정상적인 사용자인지 확인
//        accessToken = accessToken.replace(JwtProperties.TOKEN_PREFIX, "");
//
//        // 토큰 검증 (이게 인증이기 때문에 AuthenticationManager도 필요 없음)
//        // 내가 SecurityContext에 집적 접근해서 세션을 만들때 자동으로 UserDetailsService에 있는
//        // loadByUsername이 호출됨.
//        Long memId = JWT.require(Algorithm.HMAC256(JwtProperties.ACCESS_SECRET))
//                             .build()
//                             // 토큰 검증(유효기간 포험)
//                             .verify(accessToken)
//                             .getClaim("id")
//                             .asLong();
//        
//        return memId;
//	}
	
	// token 검증 & claims 추출
	public DecodedJWT getClaimsAndVerifyAccessToken(String accessToken) {
        // 토큰 검증 및 클레임 추출
        return JWT.require(Algorithm.HMAC256(JwtProperties.ACCESS_SECRET))
                 .build()
                 .verify(accessToken);
	}
	
	// refresh token 검증
	public DecodedJWT getClaimsAndVerifyRefreshToken(String refreshToken) {
        // 토큰 검증 및 클레임 추출
        return JWT.require(Algorithm.HMAC256(JwtProperties.REFRESH_SECRET))
                 .build()
                 .verify(refreshToken);
	}
	
	// 헤더에 jwt 저장
	public void saveAccessTokenInHeader(HttpServletResponse response, String accescToken) {
      response.addHeader(JwtProperties.ACCESS_HEADER_STRING, JwtProperties.TOKEN_PREFIX+accescToken);
	}

	public void saveRefreshTokenInHeader(HttpServletResponse response, String refreshToken) {
		response.addHeader(JwtProperties.REFRESH_HEADER_STRING, JwtProperties.TOKEN_PREFIX+refreshToken);
	}

	// 쿠키에 jwt 저장
	public void saveAccessTokenInCookie(HttpServletResponse response, String accescToken) {
        ResponseCookie accessCookie = ResponseCookie
        		.from(JwtProperties.ACCESS_COOKIE_STRING, accescToken)
	    	    .path("/")
//	    	    .secure(true)	 // HTTPS에서만 전송
	    	    .httpOnly(true)	 // JavaScript 접근 불가
	    	    .maxAge(JwtProperties.ACCESS_EXPIRATION_TIME / 1000) // 초단위
        	    .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
	}
	
	public void saveRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
		ResponseCookie refreshCookie = ResponseCookie
				.from(JwtProperties.REFRESH_COOKIE_STRING, refreshToken)
				.path("/")
//        		.secure(true)	 // HTTPS에서만 전송
				.httpOnly(true)	 // JavaScript 접근 불가
				.maxAge(JwtProperties.REFRESH_EXPIRATION_TIME / 1000) // 초단위
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
	}
	
	// 쿠키에 저장된 jwt 제거
	public void removeTokenInCookie(HttpServletRequest request, HttpServletResponse response) {
        // redis에서 refresh 삭제
	    String refreshToken = this.resolveTokenInCookie(request, JwtProperties.REFRESH_COOKIE_STRING);
	    System.out.println(refreshToken);
        redisTemplate.delete(refreshToken);

        // Access Token 쿠키 삭제
        ResponseCookie accessCookie = ResponseCookie
            .from(JwtProperties.ACCESS_COOKIE_STRING, "")
            .path("/")
            .maxAge(0)
            .build();

        // Refresh Token 쿠키 삭제
        ResponseCookie refreshCookie = ResponseCookie
            .from(JwtProperties.REFRESH_COOKIE_STRING, "")
            .path("/")
            .maxAge(0)
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());        
	}
	
	// 토큰들 생성 및 저장
	public void generateAndSave(HttpServletResponse response, String memId, List<String> roles) {
        // JWT 토큰 생성
        String accescToken = this.generateAccessToken(memId, roles);
        String refreshToken = this.generateRefreshToken(memId);
        
        // JWT 토큰을 만들어서 request 요청한 사용자에게 JWT 토큰을 response에 반환
        // header 방식
        this.saveAccessTokenInHeader(response, accescToken);
//        jwtProvider.saveRefreshTokenInHeader(response, refreshToken);
        
        // cookie 방식
//        jwtProvider.saveAccessTokenInCookie(response, accescToken);
        this.saveRefreshTokenInCookie(response, refreshToken);
        
		// refresh를 redis에 저장
        redisTemplate.opsForValue().set(refreshToken, memId+", "+roles, Duration.ofMinutes(JwtProperties.REFRESH_EXPIRATION_TIME / 6000));

	}
}

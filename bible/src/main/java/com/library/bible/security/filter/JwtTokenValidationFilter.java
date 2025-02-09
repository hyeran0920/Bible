package com.library.bible.security.filter;

import com.library.bible.security.jwt.JwtProperties;
import com.library.bible.security.jwt.JwtProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.library.bible.aop.PrintLog;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.exception.ExceptionResponseUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

//시큐리티가 filter 가리고 있는데, 그 필터 중에 BasicAuthenticationFilter라는 것이 있음
//권한이나 인증이 필요한 특정 주소를 요청했을 때 아래 필터를 무조건 동작함
//만약 권한이나 인증이 필요한 주소가 아니라면 동작하지 않음
public class JwtTokenValidationFilter extends BasicAuthenticationFilter {
    private JwtProvider jwtProvider;
    private final ExceptionResponseUtil exceptionResponseUtil;
	private final PrintLog printLog;

    public JwtTokenValidationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider,
    		ExceptionResponseUtil exceptionResponseUtil, PrintLog printLog) {
        super(authenticationManager);
        this.jwtProvider = jwtProvider;
        this.exceptionResponseUtil = exceptionResponseUtil;
        this.printLog = printLog;
    }

    // 인증이나 권한이 필요한 주소요청이 있을 경우 해당 필터 동작
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // header에 JWT 토큰이 존재하는지 확인
    	String accessToken = jwtProvider.resolveTokenInHeader(request, JwtProperties.ACCESS_HEADER_STRING);
        if (accessToken == null || !accessToken.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        // JWT 토큰 prefix 제거
        accessToken = accessToken.replace(JwtProperties.TOKEN_PREFIX, "");

        // cookie에 JWT 토큰이 존재하는지 확인
//    	String accessToken = jwtProvider.resolveTokenInCookie(request, JwtProperties.ACCESS_COOKIE_STRING);
//    	if(accessToken == null) {
//	          chain.doFilter(request, response);
//	          return;
//    	}

        // JWT 토큰 검증
    	Long memId = null;
    	String[] roles = null;
    	try {
    		DecodedJWT decodedJWT = jwtProvider.getClaimsAndVerifyAccessToken(accessToken);
    		memId = Long.valueOf(decodedJWT.getSubject());
    		roles = decodedJWT.getClaim("roles").asArray(String.class);
		} catch(Exception e) {
			exceptionResponseUtil.sendErrorResponse(ExceptionCode.INVALID_TOKEN_SIGNATURE, request, response, e);
			
			// logging
			printLog.printInfoByRequest(request);
			printLog.printErrorByRequest(request, e);
			return;
    	}

        if (memId != null) {            
            // SecurityContext에 인증 정보 설정
            Authentication authentication = createAuthenticationToken(memId, roles);

            // 강제로 시큐리티의 세션에 접근하여 값 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
    
    private Authentication createAuthenticationToken(Long memId, String[] roles) {
        return new UsernamePasswordAuthenticationToken(memId, null, Arrays.stream(roles)
	            .map(role -> new SimpleGrantedAuthority(role))
	            .collect(Collectors.toList()));
    }
}

package com.library.bible.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;

class AuthMemberArgumentResolverTest {

    private AuthMemberArgumentResolver resolver;
    private Authentication authentication;
    private SecurityContext securityContext;
    private MethodParameter methodParameter;
    private ModelAndViewContainer mavContainer;
    private NativeWebRequest webRequest;
    private WebDataBinderFactory binderFactory;

    @BeforeEach
    void setUp() {
        resolver = new AuthMemberArgumentResolver();
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        methodParameter = mock(MethodParameter.class);
        mavContainer = mock(ModelAndViewContainer.class);
        webRequest = mock(NativeWebRequest.class);
        binderFactory = mock(WebDataBinderFactory.class);

        SecurityContextHolder.setContext(securityContext);
    }
    @Disabled
    @Test
    void testSupportsParameter() {
        // Given
        when(methodParameter.hasParameterAnnotation(AuthMember.class)).thenReturn(true);
        System.out.println("methodParameter.hasParameterAnnotation(AuthMember.class) 실행됨");
        // When
        boolean result = resolver.supportsParameter(methodParameter);
        System.out.println("resolver.supportsParameter() 실행 결과: " + result);
        // Then
        assertThat(result).isTrue();
    }



    @Test
    void testResolveArgumentWithValidUser() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(1L);

        Object result = resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        assertThat(result).isEqualTo(1L);
    }

    @Test
    void testResolveArgumentWithAnonymousUser() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        CustomException exception = assertThrows(CustomException.class, () -> 
            resolver.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory)
        );
        assertThat(exception.getExceptionCode()).isEqualTo(ExceptionCode.UNAUTHORIZED);
    }
}

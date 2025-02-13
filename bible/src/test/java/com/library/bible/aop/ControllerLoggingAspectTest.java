package com.library.bible.aop;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

class ControllerLoggingAspectTest {

    private ControllerLoggingAspect controllerLoggingAspect;
    private JoinPoint joinPoint;
    private PrintLog printLog;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        printLog = mock(PrintLog.class);
        controllerLoggingAspect = new ControllerLoggingAspect(printLog);
        joinPoint = mock(JoinPoint.class);
    }

    @Test
    void testBeforeLogging() {
        controllerLoggingAspect.beforeControllerMethods(joinPoint);
        verify(printLog).printInfoByJoinPoint(joinPoint);
    }

    @Test
    void testAfterLogging() {
        Exception ex = new Exception("Test Exception");
        controllerLoggingAspect.afterThrowingAdvice(joinPoint, ex);
        verify(printLog).printErrorByJoinPoint(joinPoint, ex);
    }
}

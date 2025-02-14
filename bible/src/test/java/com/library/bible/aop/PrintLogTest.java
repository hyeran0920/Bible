package com.library.bible.aop;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class PrintLogTest {

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface PrintLog {
	}

    @Test
    void testPrintLogAnnotationPresence() throws NoSuchMethodException {
        Method method = this.getClass().getMethod("testMethod");
        boolean isAnnotationPresent = method.isAnnotationPresent(PrintLog.class);
        assertTrue(isAnnotationPresent);
    }
}

package com.library.bible.exception;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e, HttpServletRequest request) {
        log.error("CustomException", e);
		ExceptionCode errorCode = e.getErrorCode();
		return createErrorResponse(errorCode, request);
	}

	// MethodArgumentNotValidException 처리 (유효성 검증 실패)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationException(MethodArgumentNotValidException e,
			HttpServletRequest request) {
        log.error("MethodArgumentNotValidException", e);
		ExceptionCode errorCode = ExceptionCode.INVALID_INPUT_VALUE;
		return createErrorResponse(errorCode, request);
	}

	// HttpRequestMethodNotSupportedException 처리
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ExceptionResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException e,
			HttpServletRequest request) {
        log.error("HttpRequestMethodNotSupportedException", e);
		ExceptionCode errorCode = ExceptionCode.METHOD_NOT_ALLOWED;
		return createErrorResponse(errorCode, request);
	}

	// AccessDeniedException 처리
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException e,
			HttpServletRequest request) {
        log.error("AccessDeniedException", e);
		ExceptionCode errorCode = ExceptionCode.FORBIDDEN;
		return createErrorResponse(errorCode, request);
	}

	// SQLException 처리
	@ExceptionHandler(SQLException.class)
	public ResponseEntity<ExceptionResponse> handleSQLException(SQLException e, HttpServletRequest request) {
        log.error("SQLException", e);
		ExceptionCode errorCode = ExceptionCode.DATABASE_ERROR;
		return createErrorResponse(errorCode, request);
	}

	// 나머지 모든 예외 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleAllException(Exception e, HttpServletRequest request) {

		log.error("Unhandled exception occurred: ", e);
		ExceptionCode errorCode = ExceptionCode.INTERNAL_SERVER_ERROR;
		return createErrorResponse(errorCode, request);
	}

	// 에러 응답 생성 헬퍼 메소드
	private ResponseEntity<ExceptionResponse> createErrorResponse(ExceptionCode errorCode, HttpServletRequest request) {
		ExceptionResponse response = ExceptionResponse.builder()
				.status(errorCode.getHttpStatus().value())
				.error(errorCode.getHttpStatus().name())
				.message(errorCode.getMessage())
				.timestamp(LocalDateTime.now())
				.path(request.getRequestURI())
				.build();

		return ResponseEntity
				.status(errorCode.getHttpStatus())
				.body(response);
	}
}

package com.library.bible.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {
    // 회원 관련 에러
    MEMBER_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "회원 등록에 실패했습니다."),
    MEMBER_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "회원 수정에 실패했습니다."),
    MEMBER_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "회원 삭제에 실패했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    
    // ROLE 관련 에러
    ROLE_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "권한 등록에 실패했습니다."),
    ROLE_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "역할 수정에 실패했습니다."),
    ROLE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "역할 삭제에 실패했습니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "역할을 찾을 수 없습니다."),
    
    // Book 관련 에러 메시지
    BOOK_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "도서 등록에 실패했습니다."),
    BOOK_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "도서 수정에 실패했습니다."),
    BOOK_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "도서 삭제에 실패했습니다."),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "도서를 찾을 수 없습니다."),

    // Reservation 관련 에러 메시지
    RESERVATION_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "예약 등록에 실패했습니다."),
    RESERVATION_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "예약 수정에 실패했습니다."),
    RESERVATION_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "예약 삭제에 실패했습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),
    
    // Address 관련 에러
    ADDRESS_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "주소 등록에 실패했습니다."),
    ADDRESS_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "주소 수정에 실패했습니다."),
    ADDRESS_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "주소 삭제에 실패했습니다."),
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."),
    
    // MemberRent 관련 에러
    MEMBER_RENT_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "대여 정보 등록에 실패했습니다."),
    MEMBER_RENT_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "대여 정보 수정에 실패했습니다."),
    MEMBER_RENT_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "대여 정보 삭제에 실패했습니다."),
    MEMBER_RENT_NOT_FOUND(HttpStatus.NOT_FOUND, "대여 정보를 찾을 수 없습니다."),
    
    // 대여 관련 에러
    RENT_INSERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "대여 신청에 실패했습니다."),
    RENT_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "대여 상태 수정에 실패했습니다."),
    RENT_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "대여 취소에 실패했습니다."),
    RENT_NOT_FOUND(HttpStatus.NOT_FOUND, "대여 정보를 찾을 수 없습니다."),
    RENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 대여 중인 도서입니다."),
    RENT_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "대여 가능 권수를 초과했습니다."),

    // 대여 관련 에러
    NO_AVAILABLE_BOOK(HttpStatus.BAD_REQUEST, "현재 대여 가능한 도서가 없습니다."),
    OVERDUE_RENT(HttpStatus.BAD_REQUEST, "연체 중인 도서가 있어 대여 또는 예약이 불가능합니다."),
    EXCEEDED_RENT_LIMIT(HttpStatus.BAD_REQUEST, "대여 가능한 도서 수를 초과하였습니다."),
    ALREADY_RENTED(HttpStatus.BAD_REQUEST, "이미 대여 중인 도서입니다."),
    NOT_RENT_REQUEST_CANCELD_STATE(HttpStatus.BAD_REQUEST, "대여 신청을 취소할 수 없는 상태입니다."),
    NOT_RENT_RETURNED_STATE(HttpStatus.BAD_REQUEST, "대여 중인 도서가 아니어서 반납이 불가능한 상태입니다."),
    NOT_RENT_RENEWAL_STATE(HttpStatus.BAD_REQUEST, "대여 중인 도서가 아니어서 연장이 불가능한 상태입니다."),
    NOT_RENT_STATE(HttpStatus.BAD_REQUEST, "대여 신청하지 않은 도서에 대해서는 대여 신청으로 상태 변경할 수 없습니다."),
    RENT_OR_BOOK_ID_NOT_INPUT(HttpStatus.BAD_REQUEST, "상태를 변경할 대여 또는 책의 ID값을 입력해주세요."),
    NOT_RENT_USER(HttpStatus.UNAUTHORIZED, "대여와 관련 없는 사람의 요청입니다."),
    INVALID_RETURN_BOOK_COUNT(HttpStatus.BAD_REQUEST, "반납할 도서 수가 대여 중인 도서 수보다 많습니다."),
    EXTENSION_DENIED_DUE_TO_RESERVATION(HttpStatus.BAD_REQUEST, "도서를 예약한 사람이 있어서 연장이 불가능합니다."),
    EXTENSION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "연장 가능한 횟수를 초과했습니다."),
    NOT_POSSIBLE_SAME_BOOK(HttpStatus.BAD_REQUEST, "같은 도서를 2권이면 대여하거나 대여 신청할 수 없습니다."),
    
    // 예약 관련 비즈니스 로직 에러
    RESERVATION_ALREADY_EXISTS_CURRENT_MEMBER(HttpStatus.BAD_REQUEST, "현재 사용자가 이미 예약된 도서입니다."),
    RESERVATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 예약된 도서입니다."),
    CAN_RENT_OR_RENT_REQUEST(HttpStatus.BAD_REQUEST, "대여 신청 또는 대여 가능한 도서가 있습니다."),
    RESERVATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "예약 가능한 수량을 초과했습니다."),
    RESERVATION_OR_BOOK_ID_NOT_INPUT(HttpStatus.BAD_REQUEST, "상태를 변경할 예약 또는 책의 ID값을 입력해주세요"),
    RESERVATION_EXPIRED(HttpStatus.BAD_REQUEST, "예약 기간이 만료되었습니다."),
    RESERVATION_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "현재 예약이 불가능한 도서입니다."),
    RESERVATION_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "예약 취소가 불가능한 상태입니다."),
    
	// 인증/인가 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요한 서비스입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "해당 리소스에 대한 접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN_SIGNATURE(HttpStatus.UNAUTHORIZED, "토큰 서명이 유효하지 않습니다."),
    TOKEN_USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "토큰의 사용자를 찾을 수 없습니다."),
    
    // 인증 실패 관련
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "계정이 비활성화되었습니다."),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "계정이 잠겼습니다."),
    ACCOUNT_EXPIRED(HttpStatus.UNAUTHORIZED, "계정이 만료되었습니다."),
    CREDENTIALS_EXPIRED(HttpStatus.UNAUTHORIZED, "비밀번호가 만료되었습니다."),
	
    // 일반적인 에러
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메소드입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),

    // QR 이미지 생성 에러
	QR_IMAGE_CREATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "QR 이미지 생성 실패"),
	
	// 이메일 전송 실패
	FAIL_SEND_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패하였습니다."),
	NOT_MATCH_AUTHNUM(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 인증번호가 일치하지 않습니다.");
	
    private final HttpStatus httpStatus;
    private final String message;
}

package com.library.bible.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRequest {
	private long memId;
	private String memName;

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$", 
    		message = "비밀번호는 대소문자, 숫자, 특수기호(@$!%*?&#)를 포함한 8자 이상입니다.")
	private String memPassword;
	
    @Email(message = "유효한 이메일 형식이 아닙니다.")
	private String memEmail;

	@Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. (010-XXXX-XXXX)")
	private String memPhone;
    
    private String verifiedCode; // 인증 번호
}
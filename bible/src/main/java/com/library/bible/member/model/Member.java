package com.library.bible.member.model;

import java.util.List;

import com.library.bible.role.model.Role;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private long memId;
	private String memName;
	
	@Valid
	private String memPassword;
	
    @Valid
	private String memEmail;

    @Valid
	private String memPhone;
	
	private String memQrPath; // QR 이미지 경로
    private List<Role> roles;
}
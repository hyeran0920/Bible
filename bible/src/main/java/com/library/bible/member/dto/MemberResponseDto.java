package com.library.bible.member.dto;

import java.util.List;

import com.library.bible.member.model.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {
	private int memId;
	private String memName;
	private String memEmail;
	private String memPhone;
    private List<Role> roles;
}
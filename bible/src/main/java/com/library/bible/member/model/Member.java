package com.library.bible.member.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
	private int memId;
	private String memName;
	private String memPassword;
	private String memEmail;
    private List<Role> roles;
}
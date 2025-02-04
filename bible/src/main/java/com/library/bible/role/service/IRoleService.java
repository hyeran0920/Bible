package com.library.bible.role.service;

import java.util.List;

import com.library.bible.member.model.Member;
import com.library.bible.role.model.Role;

public interface IRoleService {
    List<Role> selectRolesByMemId(long memId);
	void insertMemberRoles(Member member);
    Role insertRole(Role role);
    void deleteRoles(long memId);
}

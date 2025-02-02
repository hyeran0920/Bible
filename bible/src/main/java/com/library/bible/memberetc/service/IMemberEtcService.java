package com.library.bible.memberetc.service;

import java.util.List;

import com.library.bible.member.model.Member;
import com.library.bible.member.model.Role;

public interface IMemberEtcService {
	void insertMemberRoles(Member member);
    List<Role> selectRolesByMemId(int memId);
    Role insertRole(Role role);
    void deleteRoles(int memId);
}

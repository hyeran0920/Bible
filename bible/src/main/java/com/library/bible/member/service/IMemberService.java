package com.library.bible.member.service;

import java.util.List;

import com.library.bible.member.model.Member;
import com.library.bible.member.model.Role;

public interface IMemberService {
    Member selectMember(int memId);
    Member selectMemberByMemEmail(String memEmail);
    List<Member> selectAllMembers();
    Member insertMember(Member member, String role);
    Member updateMember(Member member);
    void deleteMember(int memId);

    Role insertRole(Role role);
    List<Role> selectRolesByMemId(int memId);
}
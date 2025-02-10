package com.library.bible.member.service;

import java.util.List;

import com.library.bible.member.model.Member;

import jakarta.servlet.http.HttpServletRequest;

public interface IMemberService {
    Member selectMember(long memId);
	Member selectMemberByToken(HttpServletRequest request);
    Member selectMemberByMemEmail(String memEmail);
    List<Member> selectAllMembers();
    Member insertMember(Member member, String role, String verifiedCode);
    Member updateMember(Member member);
    void deleteMember(long memId);
}
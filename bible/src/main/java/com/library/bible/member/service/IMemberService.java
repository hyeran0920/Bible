package com.library.bible.member.service;

import java.util.List;

import com.library.bible.member.model.Member;

public interface IMemberService {
    Member selectMember(long memId);
    Member selectMemberByMemEmail(String memEmail);
    List<Member> selectAllMembers();
    Member insertMember(Member member, String role, String verifiedCode);
    Member updateMember(Member member);
    void deleteMember(long memId);
}
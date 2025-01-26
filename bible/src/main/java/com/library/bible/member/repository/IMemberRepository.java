package com.library.bible.member.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.member.model.Member;
import com.library.bible.member.model.Role;

@Mapper
@Repository
public interface IMemberRepository {
    Member selectMember(int memId);
    Member selectMemberByMemEmail(String memEmail);
    List<Member> selectAllMembers();
    
    // 회원 등록
    int insertMember(Member member); // member의 roles를 제외한 컬럼만 저장
    int insertMemberRoles(Member member); // member의 roles만 저장, 그 외의 컬럼은 저장하지 않음
    
    int updateMember(Member member); // member의 roles를 제외한 컬럼만 수정 
    void deleteMember(int memId);

    // 권한
    List<Role> selectRolesByMemId(int memId);
    int insertRole(Role role);
    void deleteRoles(int memId);
}

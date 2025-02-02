package com.library.bible.memberetc.respository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.member.model.Member;
import com.library.bible.memberetc.model.Role;

@Mapper
@Repository
public interface IMemberEtcRepository {
    // 회원 등록
    int insertMemberRoles(Member member); // member의 roles만 저장, 그 외의 컬럼은 저장하지 않음

    // 권한
    List<Role> selectRolesByMemId(int memId);
    int insertRole(Role role);
    void deleteRoles(int memId);
}

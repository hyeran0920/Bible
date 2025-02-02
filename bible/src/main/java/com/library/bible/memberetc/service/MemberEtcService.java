package com.library.bible.memberetc.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.member.model.Member;
import com.library.bible.member.model.Role;
import com.library.bible.memberetc.respository.IMemberEtcRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberEtcService implements IMemberEtcService {
	private final IMemberEtcRepository memberEtcRepository;

	@Override
	public void insertMemberRoles(Member member) {
		int result = memberEtcRepository.insertMemberRoles(member);
    	if(result < 1) throw new CustomException(ExceptionCode.ROLE_INSERT_FAIL);
	}
	
	@Override
	@Cacheable(value="role", key="#memId")
	public List<Role> selectRolesByMemId(int memId) {
		return memberEtcRepository.selectRolesByMemId(memId);
	}

	@Override
	public Role insertRole(Role role) {
		int result = memberEtcRepository.insertRole(role);
		if(result == 0)
			throw new CustomException(ExceptionCode.ROLE_INSERT_FAIL);
		return role;
	}

	@Override
	public void deleteRoles(int memId) {
		memberEtcRepository.deleteRoles(memId);
	}
}

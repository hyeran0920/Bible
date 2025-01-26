package com.library.bible.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.member.model.Member;
import com.library.bible.member.model.Role;
import com.library.bible.member.model.RoleName;
import com.library.bible.member.repository.IMemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements IMemberService{
	private final IMemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;

	@Override
	public Member selectMember(int memId) {
		return memberRepository.selectMember(memId);
	}

	@Override
	public Member selectMemberByMemEmail(String memEmail) {
		return memberRepository.selectMemberByMemEmail(memEmail);
	}

	@Override
	public List<Member> selectAllMembers() {
		return memberRepository.selectAllMembers();
	}

	@Override
	@Transactional
	public Member insertMember(Member member, String role) {
		try {
			// role 이외의 컬럼 저장
			member.setMemPassword(passwordEncoder.encode(member.getMemPassword())); // 비밀번호 암호화
			int memberResult = memberRepository.insertMember(member);
			// member 생성 실패
			if(memberResult != 1) throw new CustomException(ExceptionCode.MEMBER_INSERT_FAIL);
		} catch (DuplicateKeyException e) {
	        throw new CustomException(ExceptionCode.DUPLICATE_EMAIL);
	    }

		// member 권한 설정
		List<Role> roles = new ArrayList<>();
		roles.add(new Role(member.getMemId(), RoleName.ROLE_USER));
		if(role.equals("admin")) roles.add(new Role(member.getMemId(), RoleName.ROLE_ADMIN));
		member.setRoles(roles);

		// role 저장
        if (member.getRoles() != null && !member.getRoles().isEmpty()) {
        	int roleResult = memberRepository.insertMemberRoles(member);
        	if(roleResult < 1) throw new CustomException(ExceptionCode.ROLE_INSERT_FAIL);
        }

        return member;
    }

	// TODO : 테스트 필요
	@Override
	@Transactional
	public Member updateMember(Member member) {
		// role 이외의 컬럼 수정
		memberRepository.updateMember(member);
		
		// role 수정
        if (member.getRoles() != null && !member.getRoles().isEmpty()) {
        	memberRepository.deleteRoles(member.getMemId()); // 이전에 저장된 role 제거
        	memberRepository.insertMemberRoles(member); // role 추가
        }
        
        return member;
	}

	// TODO : 테스트 필요
	@Override
	@Transactional
	public void deleteMember(int memId) {
		memberRepository.deleteRoles(memId);
		memberRepository.deleteMember(memId);
	}

	@Override
	public Role insertRole(Role role) {
		memberRepository.insertRole(role);
		return role;
	}

	@Override
	public List<Role> selectRolesByMemId(int memId) {
		return memberRepository.selectRolesByMemId(memId);
	}
}
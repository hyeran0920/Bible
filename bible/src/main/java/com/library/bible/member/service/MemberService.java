package com.library.bible.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.member.model.Member;
import com.library.bible.member.repository.IMemberRepository;
import com.library.bible.memberrent.model.MemberRent;
import com.library.bible.memberrent.service.IMemberRentService;
import com.library.bible.role.model.Role;
import com.library.bible.role.model.RoleName;
import com.library.bible.role.service.IRoleService;
import com.library.bible.upload.service.UploadService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService implements IMemberService{
	private final IMemberRepository memberRepository;
	private final IRoleService roleService;
	private final IMemberRentService memberRentService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UploadService uploadService;
    private final RedisTemplate<String, String> redisTemplate;
    
	@Override
	public Member selectMember(long memId) {
		Member member = memberRepository.selectMember(memId);
		if(member == null) throw new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
		return member;
	}

	@Override
	public Member selectMemberByMemEmail(String memEmail) {
		Member member = memberRepository.selectMemberByMemEmail(memEmail);
		if(member == null) throw new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
		return member;
	}

	@Override
	public List<Member> selectAllMembers() {
		return memberRepository.selectAllMembers();
	}

	// 회원가입
	@Override
	@Transactional
	public Member insertMember(Member member, String role, String verifiedCode) {
		// 이메일 코드 검증(인증번호 확인 후 1시간 안에 회원가입해야함)
        String savedCode = redisTemplate.opsForValue().get("VERIFIED_" + member.getMemEmail());
        if(savedCode == null || !verifiedCode.equals(savedCode))
        	throw new CustomException(ExceptionCode.MUST_SIGNUP_IN_VERIVIED_EMAIL);
		
		try {
			// role 이외의 컬럼 저장
			member.setMemPassword(passwordEncoder.encode(member.getMemPassword())); // 비밀번호 암호화
			int memberResult = memberRepository.insertMember(member);
			
			// member 삽입 실패시 예외
			if(memberResult != 1) throw new CustomException(ExceptionCode.MEMBER_INSERT_FAIL);			
		} catch (DuplicateKeyException e) {
	        throw new CustomException(ExceptionCode.DUPLICATE_EMAIL);
	    }
		
		//QR이미지 생성
		uploadService.createMemberQRImage(member);
		// TODO : QR 이미지 생성 후 이미지 경로 저장하기
		
		// member 권한 설정
		List<Role> roles = new ArrayList<>();
		roles.add(new Role(member.getMemId(), RoleName.ROLE_USER));
		if(role.equals("admin")) roles.add(new Role(member.getMemId(), RoleName.ROLE_ADMIN));
		member.setRoles(roles);

		// role 저장
        if (member.getRoles() != null && !member.getRoles().isEmpty())
        	roleService.insertMemberRoles(member);
        
        // member-rent 정보 자동 생성
        MemberRent memberRent = new MemberRent(member.getMemId(), 0, 't', null);
        memberRentService.insertMemberRent(memberRent);

        return member;
    }

	@Override
	@Transactional
	public Member updateMember(Member member) {
		// role 이외의 컬럼 수정
		member.setMemPassword(passwordEncoder.encode(member.getMemPassword())); // 비밀번호 암호화
		int result = memberRepository.updateMember(member);
		if(result == 0) throw new CustomException(ExceptionCode.MEMBER_UPDATE_FAIL);
		
		// role 수정
//        if (member.getRoles() != null && !member.getRoles().isEmpty()) {
//        	memberRepository.deleteRoles(member.getMemId()); // 이전에 저장된 role 제거
//        	memberRepository.insertMemberRoles(member); // role 추가
//        }
        
        return member;
	}

	@Override
	@Transactional
	public void deleteMember(long memId) {
		roleService.deleteRoles(memId);
		memberRentService.deleteMemberRent(memId);
		memberRepository.deleteMember(memId);
		uploadService.deleteMemberQRImage(memId);
	}
}
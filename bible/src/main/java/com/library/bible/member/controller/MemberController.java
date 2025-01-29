package com.library.bible.member.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.member.dto.MemberResponseDto;
import com.library.bible.member.mapper.MemberMapper;
import com.library.bible.member.model.Member;
import com.library.bible.member.service.IMemberService;
import com.library.bible.resolver.AuthMember;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
	private final IMemberService memberService;
	private final MemberMapper memberMapper;
	
	// 일반 사용자 생성
	@PostMapping("/user")
	public ResponseEntity<MemberResponseDto> insertMember(@RequestBody @Validated Member member) {
		memberService.insertMember(member, "user");
		return ResponseEntity.status(HttpStatus.CREATED).body(memberMapper.memberToMemberResponseDto(member));
	}	
	
	// 관리자 생성
	@PostMapping("/admin")
	public ResponseEntity<MemberResponseDto> insertAdminMember(@RequestBody @Validated Member member) {
		memberService.insertMember(member, "admin");
		return ResponseEntity.status(HttpStatus.CREATED).body(memberMapper.memberToMemberResponseDto(member));
	}
	
	// 사용자 단일 조회
	@GetMapping("/{mem-id}")
	public ResponseEntity<MemberResponseDto> selectMember(@PathVariable("mem-id") int memId) {
		Member member = memberService.selectMember(memId);
		return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(member));
	}
	
	@GetMapping("/me")
	public ResponseEntity<MemberResponseDto> getMyInfo(@AuthMember Member member) {
		System.out.println(member);

	    return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(member));
	} 

	// 사용자 전체 조회
	@GetMapping
	public ResponseEntity<List<MemberResponseDto>> selectAllMembers() {
		List<Member> members = memberService.selectAllMembers();
		return ResponseEntity.ok(memberMapper.membersToMemberResponseDtos(members));
	}
	
	// 사용자 정보 변경(update)
	@PutMapping("/{memid}")
	public ResponseEntity<MemberResponseDto> updateMember(@RequestBody Member member){
		memberService.updateMember(member);
		return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(member));
	}
	
	// 사용자 정보 삭제(delete)
	@DeleteMapping("/{memid}")
	public void deleteMember(@PathVariable int memid) {
		memberService.deleteMember(memid);
	}
	
	@GetMapping("/admin-page")
	@PreAuthorize
	("hasRole('ADMIN')")//권한 체크 확인용
	public ResponseEntity<String> adminOnlyPage() {
	return ResponseEntity.ok("관리자 페이지에 접근 성공!!!");
	}
}
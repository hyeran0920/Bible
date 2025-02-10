package com.library.bible.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.member.dto.MemberRequest;
import com.library.bible.member.dto.MemberResponseDto;
import com.library.bible.member.mapper.MemberMapper;
import com.library.bible.member.model.Member;
import com.library.bible.member.service.IMemberService;
import com.library.bible.resolver.AuthMember;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
	private final IMemberService memberService;
	private final MemberMapper memberMapper;
	
	// 일반 사용자 생성
	@PostMapping("/user")
	public ResponseEntity<MemberResponseDto> insertMember(@RequestBody @Valid MemberRequest request) {
		Member member = memberMapper.memberRequestToMember(request);
		memberService.insertMember(member, "user", request.getVerifiedCode());
		return ResponseEntity.status(HttpStatus.CREATED).body(memberMapper.memberToMemberResponseDto(member));
	}	
	
	// 관리자 생성
	@PostMapping("/admin")
	public ResponseEntity<MemberResponseDto> insertAdminMember(@RequestBody @Valid MemberRequest request) {
		Member member = memberMapper.memberRequestToMember(request);
		memberService.insertMember(member, "admin", request.getVerifiedCode());
		return ResponseEntity.status(HttpStatus.CREATED).body(memberMapper.memberToMemberResponseDto(member));
	}
	
	// 사용자 단일 조회
	@GetMapping("/{memId}")
	public ResponseEntity<MemberResponseDto> selectMember(@PathVariable("memId") @Positive long memId) {
		System.out.println("!! 여기 API 사용");

		Member member = memberService.selectMember(memId);
		return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(member));
	}
	
	// 토큰으로 사용자 조회
	@GetMapping("/token")
	public ResponseEntity<MemberResponseDto> selectMemberByToken(HttpServletRequest request) {
		Member member = memberService.selectMemberByToken(request);
		return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(member));
	}
	
	@GetMapping("/me")
	public ResponseEntity<MemberResponseDto> getMyInfo(@AuthMember Long memId) {
		Member member = memberService.selectMember(memId);
	    return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(member));
	} 

	// 사용자 전체 조회
	@GetMapping
	public ResponseEntity<List<MemberResponseDto>> selectAllMembers() {
		List<Member> members = memberService.selectAllMembers();
		return ResponseEntity.ok(memberMapper.membersToMemberResponseDtos(members));
	}
	
	// 사용자 정보 변경(update)
	@PutMapping("/{memId}")
	public ResponseEntity<MemberResponseDto> updateMember(@RequestBody @Valid Member member, @PathVariable long memId){
		member.setMemId(memId);
		memberService.updateMember(member);
		return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(member));
	}

	@PutMapping("/me")
	public ResponseEntity<MemberResponseDto> updateMember(@RequestBody @Valid Member updateMember, @AuthMember Long memId){
		updateMember.setMemId(memId);
		memberService.updateMember(updateMember);
		return ResponseEntity.ok(memberMapper.memberToMemberResponseDto(updateMember));
	}
	
	// 사용자 정보 삭제(delete)
	@DeleteMapping("/{memid}")
	public void deleteMember(@PathVariable @Positive long memid) {
		memberService.deleteMember(memid);
	}

	@DeleteMapping("/me")
	public void deleteMemberByToken(@AuthMember Long memId) {
		memberService.deleteMember(memId);
	}
	
	@GetMapping("/admin-page")
	@PreAuthorize("hasRole('ADMIN')") // 권한 체크 확인용
	public ResponseEntity<Map<String, String>> adminOnlyPage(@AuthMember Long memId) { // ✅ @AuthMember로 현재 회원 정보 받기
	    Map<String, String> response = new HashMap<>();
	    String message = "관리자 페이지 접속 성공!!!";
	    response.put("message", message);

	    System.out.println(message); // 콘솔에 출력

	    return ResponseEntity.ok(response); // JSON 형태로 응답
	}
}
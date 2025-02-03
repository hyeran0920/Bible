package com.library.bible.rent.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.member.model.Member;
import com.library.bible.pageresponse.PageResponse;
import com.library.bible.rent.dto.RentHistoryResponse;
import com.library.bible.rent.model.RentHistory;
import com.library.bible.rent.model.RentStatus;
import com.library.bible.rent.service.IRentHistoryService;
import com.library.bible.resolver.AuthMember;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rent-histories")
public class RentHistoryController {
	@Autowired
	private IRentHistoryService rentHistoryService;
	
//	//대여 기록 전제 조회
//	@GetMapping
//	public ResponseEntity<List<RentHistory>> selectAllRentHistory(){
//		List<RentHistory> historys = rentHistoryService.selectAllRentHistory();
//		if(historys.isEmpty()) {
//			return ResponseEntity.noContent().build();
//		}
//		return ResponseEntity.ok(historys);
//	}
//	
//	//특정 대여 기록 조회
//	@GetMapping("{rentHistoryId}")
//	public ResponseEntity<RentHistory> selectRentHistory(@PathVariable int rentHistoryId){
//		RentHistory history = rentHistoryService.selectRentHistory(rentHistoryId);
//		return ResponseEntity.ok(history);
//	}

	// Rent의 rent_status값으로 조회 가능
	// 현재 사용자의 대여 기록 조회
	@GetMapping("/me/details")
	public ResponseEntity<PageResponse<RentHistoryResponse>> selectRentHistoryResponse(
			@AuthMember Member member,
			@RequestParam(required = false) Optional<RentStatus> rentStatus,
			@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
		PageResponse<RentHistoryResponse> pageResponsees = 
				rentHistoryService.selectRentHistoryResponses(member.getMemId(), rentStatus, PageRequest.of(page, size));
		return ResponseEntity.ok(pageResponsees);
	}

	// 대여 기록 조회 - memId=0이면 모든 사용자 정보 조회
	@GetMapping("/details")
	public ResponseEntity<PageResponse<RentHistoryResponse>> selectRentHistoryResponse(
			@RequestParam(defaultValue = "0") int memId,
			@RequestParam(required = false) Optional<RentStatus> rentStatus,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		PageResponse<RentHistoryResponse> pageResponsees = 
				rentHistoryService.selectRentHistoryResponses(memId, rentStatus, PageRequest.of(page, size));
		return ResponseEntity.ok(pageResponsees);
	}
	
	
	//대여 기록 생성
	@PostMapping
	public ResponseEntity<RentHistory> insertRentHistory(@RequestBody @Validated RentHistory history){
		rentHistoryService.insertRentHistory(history);
		return ResponseEntity.status(HttpStatus.CREATED).body(history);
	}
	
	//대여 기록 수정
	@PutMapping("{rentHistoryId}")
	public ResponseEntity<RentHistory> updateRentHistory(@PathVariable int rentHistoryId, @RequestBody RentHistory rentHistory){
		rentHistoryService.updateRentHistory(rentHistory);
		return ResponseEntity.ok(rentHistory);
	}
	
	//대여 삭제
	@DeleteMapping("{rentHistoryId}")
	public void deleteRentHistory(@PathVariable int rentHistoryId) {
		rentHistoryService.deleteRentHistory(rentHistoryId);
	}
}

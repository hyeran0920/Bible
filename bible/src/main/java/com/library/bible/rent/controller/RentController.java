package com.library.bible.rent.controller;

import java.util.List;
import java.util.Optional;

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

import com.library.bible.book.model.Book;
import com.library.bible.pageresponse.PageResponse;
import com.library.bible.rent.dto.RentPageResponse;
import com.library.bible.rent.dto.RentRequest;
import com.library.bible.rent.dto.RentResponse;
import com.library.bible.rent.mapper.RentMapper;
import com.library.bible.rent.model.Rent;
import com.library.bible.rent.model.RentStatus;
import com.library.bible.rent.service.IRentService;
import com.library.bible.resolver.AuthMember;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rents")
public class RentController {
	private final RentMapper rentMapper;
	private final IRentService rentService;
	
	// 대여 전체 조회
//	@GetMapping
//	public ResponseEntity<List<Rent>> selectAllRent(){
//		List<Rent> rents = rentService.selectAllRent();
//		//데이터가 없을 경우 204 no Content 반환
//		if(rents.isEmpty()) {
//			return ResponseEntity.noContent().build();
//		}
//		return ResponseEntity.ok(rents);
//	}
	
	//특정 대여 조회
	@GetMapping("{rentId}")
	public ResponseEntity<Rent> selectRent(@PathVariable @Positive long rentId){
		Rent rent = rentService.selectRent(rentId);
		return ResponseEntity.ok(rent);
	}
	
	// Rent의 rent_status값으로 조회 가능
	// 현재 사용자의 대여 기록 조회
	@GetMapping("/me")
	public ResponseEntity<PageResponse<RentPageResponse>> selectRentHistoryResponse(
			@AuthMember Long memId,
			@RequestParam(required = false) Optional<RentStatus> rentStatus,
			@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
		PageResponse<RentPageResponse> pageResponsees = 
				rentService.selectRentResponses(memId, rentStatus, PageRequest.of(page, size));
		return ResponseEntity.ok(pageResponsees);
	}
	
	@GetMapping("/member/{memId}")
	public ResponseEntity<List<Book>> selectRentedBooks(@PathVariable long memId){
		List<Book>books=rentService.getRentedBooks(memId);
		System.out.println("get books="+books.size());
		System.out.println(books);
		return ResponseEntity.ok(books);
	}


	// 대여 기록 조회 - memId=0이면 모든 사용자 정보 조회
	@GetMapping
	public ResponseEntity<PageResponse<RentPageResponse>> selectRentHistoryResponse(
			@RequestParam(defaultValue = "0") int memId,
			@RequestParam(required = false) Optional<RentStatus> rentStatus,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		PageResponse<RentPageResponse> pageResponsees = 
				rentService.selectRentResponses(memId, rentStatus, PageRequest.of(page, size));
		return ResponseEntity.ok(pageResponsees);
	}
	
	//대여 생성
	@PostMapping
	public ResponseEntity<Rent> insertRent(@RequestBody @Validated Rent rent) {
		rentService.insertRent(rent);
		return ResponseEntity.status(HttpStatus.CREATED).body(rent);
	}
	
	// 1. 대여 신청하기 - 사용자
	@PostMapping("/requests/me")
	public ResponseEntity<List<RentResponse>> insertRents(@AuthMember Long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.insertRequestRents(memId, request.getBookIds(), RentStatus.REQUESTED);
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.status(HttpStatus.CREATED).body(responses);
	}
	
	//대여 수정
	@PutMapping("{rentId}")
	public ResponseEntity<Rent> updateRent(@PathVariable long rentId, @RequestBody Rent rent){
		rentService.updateRent(rent);
		return ResponseEntity.ok(rent);
	}
	
	// 2. 대여 신청 취소하기 - 사용자
	@PutMapping("/cancels/me")
	public ResponseEntity<List<RentResponse>> updateCancledRentByToken(@AuthMember Long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.updateCancledRent(memId, request.getRentIds());
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.ok(responses);		
	}
	
	// 2. 대여 신청 취소하기 - 관리자
	@PutMapping("/cancels")
	public ResponseEntity<List<RentResponse>> updateCancledRent(@RequestParam @Positive long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.updateCancledRent(memId, request.getRentIds());
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.ok(responses);
	}
	
	// 3. 대여하기 - 관리자
	@PutMapping("/rentals")
	public ResponseEntity<List<RentResponse>> updateRentedRent(@RequestParam @Positive long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.insertAndUpdateRentalRents(memId, request.getBookIds(), RentStatus.IN_USE);
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.ok(responses);
	}
	
	// 4. 연장하기 - 사용자
	@PutMapping("/renewals/me")
	public ResponseEntity<List<RentResponse>> updateRenewaledRentByToken(@AuthMember Long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.updateRenewalRent(memId, request.getRentIds());
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.ok(responses);		
	}
	
	// 4. 연장하기 - 관리자
	@PutMapping("/renewals")
	public ResponseEntity<List<RentResponse>> updateRenewaledRent(@RequestParam @Positive long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.updateRenewalRent(memId, request.getRentIds());
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.ok(responses);		
	}	
	
	// 5. 반납하기 - 관리자
	@PutMapping("/returns")
	public ResponseEntity<List<RentResponse>> updateReturnedRent(@RequestParam @Positive long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.updateReturnedRent(memId, request.getBookIds());
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.ok(responses);
	}	
	
	// 6. 대여와 반납 동시에 - 관리자
	@PutMapping("/rentals-returns")
	public ResponseEntity<List<RentResponse>> updateRentedReturnedRent(@RequestParam @Positive long memId, @RequestBody RentRequest request) {
		List<Rent> rentHistoryResponse = rentService.updateRentedReturnedRent(memId, request.getBookIds());
		List<RentResponse> responses = rentMapper.rentsToRentResponses(rentHistoryResponse);
		return ResponseEntity.ok(responses);
	}	
	
	// 대여 삭제
	@DeleteMapping("{rentId}")
	public void deleteRent(@PathVariable @Positive long rentId) {
		rentService.deleteRent(rentId);
	}
	
	// 연체일 체크
	@GetMapping("/check")
	public ResponseEntity<List<String>> checkOverdueBooks() {
	    List<String> overdueMessages = rentService.processOverdueBooks();
	    return ResponseEntity.ok(overdueMessages);
	}	
}

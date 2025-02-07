package com.library.bible.reservation.controller;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.library.bible.reservation.dto.ReservationRequest;
import com.library.bible.reservation.dto.ReservationResponse;
import com.library.bible.reservation.model.Reservation;
import com.library.bible.reservation.service.IReservationService;
import com.library.bible.resolver.AuthMember;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
	private final IReservationService reservService;
	private final ReentrantLock lock = new ReentrantLock();		//동시성 lock 처리
	
	//예약 전체 조회
	@GetMapping("/all")
	public ResponseEntity<List<ReservationResponse>> selectAllReserv(){
		List<ReservationResponse> reservs = reservService.selectReservResponsesByMemId(null);
		if(reservs.isEmpty()) return ResponseEntity.noContent().build();
		return ResponseEntity.ok(reservs);
	}
	
	// 특정 예약 조회
	@GetMapping("/{reservId}")
	public ResponseEntity<Reservation> selectReserv(@PathVariable @Positive long reservId){
		Reservation reserv = reservService.selectReserv(reservId);
		return ResponseEntity.ok(reserv);
	}
	
	// 1. 예약 조회 - 사용자
	@GetMapping("/me")
	public ResponseEntity<List<ReservationResponse>> selectReservByMemId(@AuthMember Member member){
		List<ReservationResponse> reservs = reservService.selectReservResponsesByMemId(member.getMemId());
		return ResponseEntity.ok(reservs);
	}	

	// 1. 예약 조회 - 관리자
	@GetMapping
	public ResponseEntity<List<ReservationResponse>> selectReservByMemId(@RequestParam long memId){
		List<ReservationResponse> reservs = reservService.selectReservResponsesByMemId(memId);
		return ResponseEntity.ok(reservs);
	}	
	
//	// 예약 생성
//	@PostMapping
//	public ResponseEntity<Reservation> insertReserv(@RequestBody @Validated Reservation reservation){
//		lock.lock();
//		try {
//			reservService.insertReserv(reservation);
//			return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
//		} finally {
//			lock.unlock();
//		}
//	}
	
	// 2. 예약 생성 - bookIds로 예약 생성하기(사용자)
	@PostMapping("/me")
	public ResponseEntity<List<Reservation>> insertReservByMemId(@AuthMember Member member, @RequestBody ReservationRequest request) {
		lock.lock();
		try {
			List<Reservation> reservations = reservService.insertReservByBookIds(request.getBookIds(), member.getMemId());
			System.out.println(reservations);

			return ResponseEntity.status(HttpStatus.CREATED).body(reservations);
		} finally {
			lock.unlock();
		}
	}
	
	// 2. 예약 생성 - bookIds로 예약 생성하기(관리자)
	@PostMapping
	public ResponseEntity<List<Reservation>> insertReservByMemId(@RequestParam long memId, @RequestBody ReservationRequest request) {
		lock.lock();
		try {
			List<Reservation> reservations = reservService.insertReservByBookIds(request.getBookIds(), memId);
			System.out.println(reservations);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(reservations);
		} finally {
			lock.unlock();
		}
	}
	
	// 예약 수정
	@PutMapping("{reservId}")
	public ResponseEntity<Reservation> updateReserv(@PathVariable long reservId, @RequestBody Reservation reservation){
		Reservation existingReserv = reservService.selectReserv(reservId);
		if(existingReserv == null) {
			return ResponseEntity.noContent().build();
		}
		reservService.updateReserv(reservation);
		return ResponseEntity.ok(reservation);
	}
	
	// 예약 삭제
	@DeleteMapping("{reservId}")
	public ResponseEntity<?> deleteReserv(@PathVariable @Positive long reservId) {
		reservService.deleteReserv(reservId);
		return ResponseEntity.noContent().build();
	}
	
	// 3. 여러 개의 예약 삭제 - 사용자
	@PostMapping("/deletion/me")
	public ResponseEntity<?> deleteReservs(@AuthMember Member member, @RequestBody ReservationRequest request) {
		reservService.deleteReservsByMemId(request.getReservIds(), member.getMemId());
		return ResponseEntity.noContent().build();
	}
	
	// 3. 여러 개의 예약 삭제 - 관리자
	@PostMapping("/deletion")
	public ResponseEntity<?> deleteReservs(@RequestBody ReservationRequest request) {
		reservService.deleteReservs(request.getReservIds());
		return ResponseEntity.noContent().build();
	}
}

package com.library.bible.reservation.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.library.bible.book.dto.BookAndReservationInfo;
import com.library.bible.book.service.IBookService;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.memberrent.model.MemberRent;
import com.library.bible.memberrent.service.IMemberRentService;
import com.library.bible.reservation.config.ReservationProperties;
import com.library.bible.reservation.model.Reservation;
import com.library.bible.reservation.repository.IReservationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {
	private final IMemberRentService memberRentService;
	private final IBookService bookService;
	private final IReservationRepository reservRepository;
	
	@Override
	@Cacheable(value="reserv")
	public List<Reservation> selectAllReserv() {
		return reservRepository.selectAllReserv();
	}
	
	// 사용자 id로 예약들 조회하기
	@Override
	public List<Reservation> selectReservByMemId(long memId) {
		return reservRepository.selectReservByMemId(memId);
	}
	
	// 책 id들로 예약들 조회하기
	@Override
	public List<Reservation> selectReservByBookIds(List<Long> bookIds) {
		List<Reservation> reservs = reservRepository.selectReservByBookIds(bookIds);
		return reservs;
	}

	@Override
	@Cacheable(value="reserv", key="#reservId")
	public Reservation selectReserv(long reservId) {
		Reservation reservs = reservRepository.selectReserv(reservId);
		if(reservs == null)
			throw new CustomException(ExceptionCode.RESERVATION_NOT_FOUND);
		return reservs;
	}

	@Override
	@Transactional
	@CacheEvict(value="reservs", allEntries=true)
	public Reservation insertReserv(Reservation reservation) {
		int result = reservRepository.insertReserv(reservation);
		if(result == 0) throw new CustomException(ExceptionCode.RESERVATION_INSERT_FAIL);
		return reservation;
	}
	
	// bookIds로 예약 생성하기
	@Override
	@Transactional
	@CacheEvict(value="reservs", allEntries=true)
	public List<Reservation> insertReservByBookIds(List<Long> bookIds, long memId) {
		// 연체 중이면 대여 신청 불가능
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);
		if(memberRent.getRentPoss() == 'f') 
			throw new CustomException(ExceptionCode.OVERDUE_RENT);
		
		// bookIds의 예약 정보와 memId에 해당하는 예약 정보 전부 가져오기
		List<BookAndReservationInfo> bookAndReservationInfos = 
				bookService.getBookAndReservations(bookIds, memId);

		// 예약 가능한 횟수를 초과한 경우 -> 불가능
		int reservCnt = (int) bookAndReservationInfos.stream()
				.filter(b -> b.getReservMemId() != null && b.getReservMemId().equals(memId))
				.count();
		if(reservCnt >= ReservationProperties.POSSIBLE_BOOK_COUNT.getValue())
			throw new CustomException(ExceptionCode.RESERVATION_LIMIT_EXCEEDED);

		List<Reservation> reservations = new ArrayList<>();
		for(BookAndReservationInfo bookAndReservationInfo : bookAndReservationInfos) {
			if(!bookIds.contains(bookAndReservationInfo.getBook().getBookId())) continue;
			
			if(bookAndReservationInfo.getReservId() != null) {
				// 현재 사용자가 예약 중인 도서 -> 불가능
				if(bookAndReservationInfo.getReservMemId() == memId)
					throw new CustomException(ExceptionCode.RESERVATION_ALREADY_EXISTS_CURRENT_MEMBER);					
				else // 이미 예약되어 있는 도서인 경우 -> 불가능
					throw new CustomException(ExceptionCode.RESERVATION_ALREADY_EXISTS);					
			}
			
			// 대여 신청 또는 대여 가능한 도서가 있는 경우 -> 불가능
			if(bookAndReservationInfo.getBook().getBookTotalStock() - bookAndReservationInfo.getBook().getBookRentStock() != 0) {
				throw new CustomException(ExceptionCode.CAN_RENT_OR_RENT_REQUEST);					
			}
			
			// 예약 생성 후 저장
			Reservation reservation = Reservation.builder()
					.memId(memId)
					.bookId(bookAndReservationInfo.getBook().getBookId())
					.reservDate(new Timestamp(System.currentTimeMillis()))
					.build();
			this.insertReserv(reservation);
			reservations.add(reservation);
		}
		
		return reservations;
	}

	@Override
	@Transactional
	@CachePut(value="reserv", key="#reservation.reservId")
	public Reservation updateReserv(Reservation reservation) {
		int result = reservRepository.updateReserv(reservation);
		if(result == 0) throw new CustomException(ExceptionCode.RESERVATION_UPDATE_FAIL);
		return reservation;
	}

	@Override
	@Transactional
	@Caching(evict= {
		@CacheEvict(value="reserv", key="#reservId"),
		@CacheEvict(value="reservs", allEntries=true)
	})
	public int deleteReserv(long reservId) {
		int result = reservRepository.deleteReserv(reservId);
		if(result == 0) throw new CustomException(ExceptionCode.RESERVATION_DELETE_FAIL);
		return result;
	}
	
	// reservIds를 이용해서 한 번에 예약 삭제
	@Override
	@Transactional
	@CacheEvict(value="reservs", allEntries=true)
	public int deleteReservs(List<Long> reservIds) {
		int result = reservRepository.deleteReservs(reservIds);
		if(result != reservIds.size()) throw new CustomException(ExceptionCode.RESERVATION_DELETE_FAIL);
		return result;
	}
	
	// 해당 사용자의 reservIds에 해당하는 예약 삭제
	@Override
	@Transactional
	public int deleteReservsByMemId(List<Long> reservIds, Long memId) {
		if(reservIds == null || reservIds.isEmpty())
			throw new CustomException(ExceptionCode.RESERVATION_OR_BOOK_ID_NOT_INPUT);
		int result = reservRepository.deleteReservsByMemId(reservIds, memId);
		if(result != reservIds.size()) throw new CustomException(ExceptionCode.RESERVATION_DELETE_FAIL);
		return result;
	}

}

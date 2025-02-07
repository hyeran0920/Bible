package com.library.bible.rent.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.library.bible.book.model.Book;
import com.library.bible.book.service.IBookService;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.memberrent.model.MemberRent;
import com.library.bible.memberrent.service.IMemberRentService;
import com.library.bible.pageresponse.PageResponse;
import com.library.bible.rent.config.CheckRentAbout;
import com.library.bible.rent.config.RentProperties;
import com.library.bible.rent.dto.RentMemberResponse;
import com.library.bible.rent.dto.RentPageResponse;
import com.library.bible.rent.model.Rent;
import com.library.bible.rent.model.RentStatus;
import com.library.bible.rent.repository.IRentRepository;
import com.library.bible.reservation.model.Reservation;
import com.library.bible.reservation.service.IReservationService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentService implements IRentService {
	private final IBookService bookService;
	private final IReservationService reservationService;
	private final IMemberRentService memberRentService;
	private final IRentRepository rentRepository;

	@Override
	@Cacheable(value="rent", key="#rentId")
	public Rent selectRent(long rentId) {
		Rent rent = rentRepository.selectRent(rentId);
		if(rent == null) throw new CustomException(ExceptionCode.RENT_NOT_FOUND);
		return rent;
	}

	@Override
	@Cacheable(value="rents")
	public List<Rent> selectAllRent() {
		return rentRepository.selectAllRent();
	}
	
	@Override
	public List<Rent> selectRentsByRendIds(List<Long> rentIds) {
		List<Rent> rents = rentRepository.selectRentsByRendIds(rentIds);
		if(rents.size() != rentIds.size())
			throw new CustomException(ExceptionCode.RENT_NOT_FOUND);
		return rents;
	}
	
	// memId, rentStatus로 조회 후 페이지네이션
	@Override
	@Transactional
	public PageResponse<RentPageResponse> selectRentResponses(long memId, Optional<RentStatus> rentStatus,
			PageRequest pageRequest) {
		String status = rentStatus
		    .map(RentStatus::toString) // 값이 있으면 toString() 호출
		    .orElse(null);  // 값이 없으면 기본값 반환

		List<RentPageResponse> content = rentRepository.selectRentResponses(
			memId,
			status,
			pageRequest.getPageNumber() * pageRequest.getPageSize(),
			pageRequest.getPageSize()
		);
		
		int total = rentRepository.countRent(memId, status);
		
		int totalPages = (int) Math.ceil((double) total / pageRequest.getPageSize());
		boolean isLast = pageRequest.getPageNumber() >= totalPages - 1;
		
		return new PageResponse<RentPageResponse>(content, totalPages, total, isLast);
	}
	
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public void insertRent(Rent rent) {
		int result = rentRepository.insertRent(rent);
		if(result == 0) throw new CustomException(ExceptionCode.RENT_INSERT_FAIL);
	}
	
	@Override
	@Transactional
	public List<Rent> insertRents(List<Rent> rents) {
		for(Rent rent : rents) {
			this.insertRent(rent);
		}
		return rents;
	}
	
	// 책 ID로 rent 생성하기 -> 대여 신청하기
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> insertRequestRents(long memId, List<Long> bookIds, RentStatus rentStatus) {
		// 대여 신청할 id값들이 없는 경우
		if(isListNullOrEmpty(bookIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);
		
		// 같은 도서가 2권 이상 들어왔을 겨우
		if(bookIds.stream().distinct().count() != bookIds.size())
			throw new CustomException(ExceptionCode.NOT_POSSIBLE_SAME_BOOK);
		
		// 현재 사용자가 대여 신청 또는 대여 중인 rents 조회
		List<Rent> currentRents = rentRepository.selectRentsByMemIdAndRentStatusList(
				memId, 
				List.of(RentStatus.REQUESTED.toString(), RentStatus.IN_USE.toString())
		);

		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);
		// 사용자가 대여 중인 도서 수 업데이트
		memberRent.setTotalRentCount(memberRent.getTotalRentCount() + bookIds.size());
		
		// 실제 존재하는 책인지 확인
		List<Book> books = bookService.getBookListByBookIds(bookIds);
		// 대여 신청할 도서 상태 변경 & 대여 생성
		List<Rent> rents = updateBookRentStatusAndcreateRent(books, memberRent, currentRents, rentStatus, this.geCurrentTimestamps());
		
		// 대여 정보 변경사항 반영
		memberRentService.updateMemberRent(memberRent);

		// 도서 업데이트
		bookService.updateBookRentStocks(books);
		
		return rents;
	}
	
	// 책 ID로 rent 생성하기 -> 대여하기, 대여 신청한 도서를 대여로 변경
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> insertAndUpdateRentalRents(long memId, List<Long> bookIds, RentStatus rentStatus) {
		// 대여할 id값들이 없는 경우
		if(isListNullOrEmpty(bookIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);

		// 같은 도서가 2권 이상 들어왔을 겨우
		if(bookIds.stream().distinct().count() != bookIds.size())
			throw new CustomException(ExceptionCode.NOT_POSSIBLE_SAME_BOOK);
		
		// 현재 사용자가 대여 신청 또는 대여 중인 rents 조회
		List<Rent> currentRents = rentRepository.selectRentsByMemIdAndRentStatusList(
				memId, 
				List.of(RentStatus.REQUESTED.toString(), RentStatus.IN_USE.toString())
		);
		
		// 대여 중인 도서를 대여하려는 경우 -> 예외 발생
		if (currentRents.stream()
		        .anyMatch(rent -> bookIds.contains(rent.getBookId()) && RentStatus.IN_USE == rent.getRentStatus())) {
		    throw new CustomException(ExceptionCode.ALREADY_RENTED);
		}
		
		// 대여 시간과 반납 시간 설정
		List<Timestamp> dates = this.geCurrentTimestamps();
		
		// 대여 신청한 도서의 대여
		List<Rent> rents = currentRents.stream()
			    .filter(rent -> bookIds.contains(rent.getBookId()) && RentStatus.REQUESTED == rent.getRentStatus())
			    .collect(Collectors.toList());
		// 대여 신청 중인 도서를 대여 중으로 변경
		if(!rents.isEmpty()) {
			this.updateRentedRentInfo(memId, rents, dates);
			this.updateRents(rents); // DB에 저장
		}
				
		// 대여 신청하지 않은 도서
		List<Long> nonRequestBookIds = bookIds.stream()
			    .filter(bookId -> currentRents.stream().noneMatch(rent -> rent.getBookId() == bookId))
			    .collect(Collectors.toList());

		// 대여 신청 안한 도서의 상태 변경 & 대여 생성
		if(!isListNullOrEmpty(nonRequestBookIds)) {
			// 현재 사용자의 대여 정보 확인
			MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);

			// 사용자가 대여 중인 도서 수 업데이트
			memberRent.setTotalRentCount(memberRent.getTotalRentCount() + nonRequestBookIds.size());
				
			// 실제 존재하는 책인지 확인
			List<Book> nonRequestBooks = bookService.getBookListByBookIds(nonRequestBookIds);

			// 대여 신청 안한 도서의 상태 변경 & 대여 생성
			rents.addAll(updateBookRentStatusAndcreateRent(nonRequestBooks, memberRent, currentRents, rentStatus, dates));

			// 도서 업데이트
			bookService.updateBookRentStocks(nonRequestBooks);

			// 대여 정보 변경사항 반영
			memberRentService.updateMemberRent(memberRent);
		}
		
		return rents;
	}

	@Override
	@Transactional
	@CachePut(value = "rent", key = "#rent.rentId")
	public void updateRent(Rent rent) {
		int result = rentRepository.updateRent(rent);
		if(result == 0) throw new CustomException(ExceptionCode.RENT_UPDATE_FAIL);
	}

	@Override
	@Transactional
	public void updateRents(List<Rent> rents) {
		int result = rentRepository.updateRents(rents);
		if(result != rents.size()) throw new CustomException(ExceptionCode.RENT_UPDATE_FAIL);
	}
	
	// 대여 신청 취소하기
	@Override
	@Transactional
	@CachePut(value = "rent", key = "#rentIds")
	public List<Rent> updateCancledRent(long memId, List<Long> rentIds) {
		// 취소할 대여 id값들이 없는 경우
		if(isListNullOrEmpty(rentIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);
		
		// rentIds로 rent 조회하기
		List<Rent> rents = this.selectRentsByRendIds(rentIds);
		
		// 대여 취소 조건 확인
		CheckRentAbout.checkCancledRentPossible(memId, rents, RentStatus.REQUESTED, ExceptionCode.NOT_RENT_REQUEST_CANCELD_STATE);
		// 대여 내역을 취소 상태로 변경
		rents.stream().forEach(rent -> rent.setRentStatus(RentStatus.CANCLED));
		this.updateRents(rents);
		
		// 대여 신청한 도서 조회
		List<Book> books = bookService.getBookListByRentIds(rentIds);
		// book에 대여 중인 도서수 감소
		books.stream().forEach(book -> book.setBookRentStock(book.getBookRentStock() - 1));		
		// 도서 업데이트
		bookService.updateBookRentStocks(books);

		// member rent 정보 업데이트 - 현재 대여 중인 도서수 감소
		memberRentService.updateTotalRentCount(memId, books.size());

		return rents;
	}
	
	// 대여 신청한 도서의 대여하기
	@Transactional
	@CachePut(value = "rent", key = "#rentIds")
	public List<Rent> updateRentedRentInfo(long memId, List<Rent> rents, List<Timestamp> dates) {
		// 대여할 대여 id값들이 없는 경우
		if(rents == null || rents.isEmpty()) return rents;

		// 도서 대여 조건 확인
		CheckRentAbout.checkCancledRentPossible(memId, rents, RentStatus.REQUESTED, ExceptionCode.NOT_RENT_STATE);

		// 대여 내역을 대여 상태로 변경
		rents.stream().forEach(rent -> {
			rent.setRentStatus(RentStatus.IN_USE);
			rent.setRentDate(dates.get(0));
			rent.setRentDueDate(dates.get(1));
		});

		return rents;
	}	
	
	// 대여한 도서 반납하기
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> updateReturnedRent(long memId, List<Long> bookIds) {
		// 반납할 대여 id값들이 없는 경우
		if(isListNullOrEmpty(bookIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);
		
		// 같은 도서가 2권 이상 들어왔을 겨우
		if(bookIds.stream().distinct().count() != bookIds.size())
			throw new CustomException(ExceptionCode.NOT_POSSIBLE_SAME_BOOK);

		// memId로 대여 중인 rent 조회하기
		List<Rent> rents = rentRepository.selectRentsByMemIdAndRentStatus(
				memId, RentStatus.IN_USE.toString()
		);
		// 대여 신청한 도서가 아닌 경우
		if (rents.stream().anyMatch(rent -> rent.getRentStatus() != RentStatus.IN_USE))
			throw new CustomException(ExceptionCode.NOT_RENT_REQUEST_CANCELD_STATE);
		// 대여 중인 도서 rents 보다 bookIds가 많으면 에러 발생
		if(rents.size() < bookIds.size())
			throw new CustomException(ExceptionCode.INVALID_RETURN_BOOK_COUNT);
		// 대여 내역을 반납 상태로 변경 & 반납 시간
		Timestamp rentFinishDate = new Timestamp(System.currentTimeMillis());
		rents.stream().forEach(rent -> {
			rent.setRentStatus(RentStatus.RETURNED);
			rent.setRentFinishDate(rentFinishDate);
		});
		this.updateRents(rents);
		
		// 반납 신청한 도서 조회
		List<Book> books = bookService.getBookListByBookIds(bookIds);
		// book에 대여 중인 도서수 감소
		books.stream().forEach(book -> book.setBookRentStock(book.getBookRentStock() - 1));		

		// 반납 시 예약 삭제 및 rent 생성
		deleteReservationAndInsertRent(bookIds, books, this.geCurrentTimestamps().get(0));
		
		// 도서 업데이트
		bookService.updateBookRentStocks(books);	

		// member rent 정보 업데이트 - 현재 대여 중인 도서수 감소
		memberRentService.updateTotalRentCount(memId, books.size());

		return rents;
	}
	
	// 연장하기
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> updateRenewalRent(long memId, List<Long> rentIds) {
		// 대여 연장할 bookId 값들이 없는 경우
		if(isListNullOrEmpty(rentIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);
		
		// rentIds로 대여 중인 rent 조회하기
		List<Rent> rents = this.selectRentsByRendIds(rentIds);

		// 공통 조건 확인
		CheckRentAbout.checkCancledRentPossible(memId, rents, RentStatus.IN_USE, ExceptionCode.NOT_RENT_RENEWAL_STATE);

		// 대여 중인 도서의 예약 정보 가져오기
		List<Reservation> reservations = new ArrayList<>();
		List<Long> bookIds = rents.stream().map(rent -> rent.getBookId()).toList();
		reservations = reservationService.selectReservByBookIds(bookIds);

		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);
		// rentIds로 book 조회
		List<Book> books = bookService.getBookListByRentIds(rentIds);
		
		long rewalDate = TimeUnit.DAYS.toMillis(RentProperties.RENEWAL_DATE.getValue());
		for(Rent rent : rents) {
			// 대여 날짜 연장
			rent.setRentDueDate(new Timestamp(rent.getRentDueDate().getTime() + rewalDate));

			// 연장 가능 여부 확인
			CheckRentAbout.checkRenewalBookPossible(rent, books, memberRent, reservations);
		}
		this.updateRents(rents);

		return rents;
	}
	
	// 대여 및 반납하기
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> updateRentedReturnedRent(long memId, List<Long> bookIds) {
		// 반납하기

		// 반납 또는 대여할 id값들이 없는 경우
		if(isListNullOrEmpty(bookIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);

		// 같은 도서가 2권 이상 들어왔을 경우
		if(bookIds.stream().distinct().count() != bookIds.size())
			throw new CustomException(ExceptionCode.NOT_POSSIBLE_SAME_BOOK);

		// 현재 시간과 반납 시간 설정
		List<Timestamp> dates = this.geCurrentTimestamps();
		
		// 현재 사용자가 대여 신청 또는 대여 중인 rents 조회
		List<Rent> memberRents = rentRepository.selectRentsByMemIdAndRentStatusList(
				memId, 
				List.of(RentStatus.REQUESTED.toString(), RentStatus.IN_USE.toString())
		);
		List<Book> returedBooks = new ArrayList<>();
				
		// 현재 대여 중인 rent 필터링
		List<Rent> returedRents = memberRents.stream()
				.filter(rent -> bookIds.contains(rent.getBookId()) && RentStatus.IN_USE == rent.getRentStatus())
				.collect(Collectors.toList());
		
		// 반납할 도서가 있을 경우
		if(returedRents != null && !returedRents.isEmpty()) {
			// 대여 내역을 반납 상태로 변경 & 반납 시간
			returedRents.stream().forEach(rent -> {
				rent.setRentStatus(RentStatus.RETURNED);
				rent.setRentFinishDate(dates.get(0));
			});
			
			// 대여한 도서 조회
			List<Long> returedBookIds = returedRents.stream().map(Rent::getBookId).collect(Collectors.toList());
			returedBooks = bookService.getBookListByBookIds(returedBookIds);
			// book에 대여 중인 도서수 감소
			returedBooks.stream().forEach(book -> book.setBookRentStock(book.getBookRentStock() - 1));		
			
			// 반납 시 예약 삭제 및 rent 생성
			deleteReservationAndInsertRent(returedBookIds, returedBooks, dates.get(0));
		}

		// 대여하기 --------------------------------------------------------------------------
		// 대여 신청한 도서의 대여
		List<Rent> requestRents = memberRents.stream()
			    .filter(rent -> bookIds.contains(rent.getBookId()) && RentStatus.REQUESTED == rent.getRentStatus())
			    .collect(Collectors.toList());
		// 대여 신청 중인 도서를 대여 중으로 변경
		if(!requestRents.isEmpty()) this.updateRentedRentInfo(memId, requestRents, dates);
		if(!returedBooks.isEmpty()) requestRents.addAll(returedRents); // 반납한 값을 반환값에 넣기
		if(!requestRents.isEmpty()) this.updateRents(requestRents); // rent 업데이트(반납한 도서 대여 + 대여 신청한 대여)
		
		// 대여 신청하지 않은 도서
		List<Long> nonRequestBookIds = bookIds.stream()
			    .filter(bookId -> memberRents.stream().noneMatch(rent -> rent.getBookId() == bookId))
			    .collect(Collectors.toList());

		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);		

		// 사용자가 대여 중인 도서 수 업데이트
		memberRent.setTotalRentCount(memberRent.getTotalRentCount() + nonRequestBookIds.size() - returedBooks.size());

		// 대여 신청 안한 도서의 상태 변경 & 대여 생성
		List<Book> nonRequestBooks = new ArrayList<>();
		if(!isListNullOrEmpty(nonRequestBookIds)) {
			// 실제 존재하는 책인지 확인
			nonRequestBooks = bookService.getBookListByBookIds(nonRequestBookIds);

			requestRents.addAll(updateBookRentStatusAndcreateRent(nonRequestBooks, memberRent, requestRents, RentStatus.IN_USE, dates));			
		}

		// 대여 정보 변경사항 반영
		if(nonRequestBookIds.size() != returedBooks.size()) memberRentService.updateMemberRent(memberRent);		
				
		// 도서 업데이트
		if(!nonRequestBooks.isEmpty()) returedBooks.addAll(nonRequestBooks);
		if(!returedBooks.isEmpty()) bookService.updateBookRentStocks(returedBooks);
		
		return requestRents;
	}

	@Override
	@Transactional
	@Caching(evict= {
		@CacheEvict(value = "rent", key = "#rentId"),
		@CacheEvict(value = "rents", allEntries = true)
	})
	public int deleteRent(long rentId) {
		int result = rentRepository.deleteRent(rentId);
		if(result == 0) throw new CustomException(ExceptionCode.RENT_DELETE_FAIL);
		return result;
	}
	
	// list가 비어있는지 확인
	private boolean isListNullOrEmpty(List<Long> list) {
		if(list == null || list.isEmpty()) return true;
		return false;
	}
	
	// 도서 상태 변경 & 대여 생성 -> 대여 신청 또는 대여할 도서에만 해당
	private List<Rent> updateBookRentStatusAndcreateRent(List<Book> books, MemberRent memberRent, 
			List<Rent> currentRents, RentStatus rentStatus, List<Timestamp> dates) {
		List<Rent> rents = new ArrayList<>();

		for(Book book : books) {
			// 현재 도서가 대여 가능한지 확인
			CheckRentAbout.checkRentBookPossible(book, memberRent, currentRents);
			book.setBookRentStock(book.getBookRentStock()+1); // 대여 중인 도서개수 추가

			// rent 생성
			Rent rent = new Rent();
			rent.setBookId(book.getBookId());
			rent.setMemId(memberRent.getMemId());
			rent.setRentDate(dates.get(0));
			rent.setRentDueDate(dates.get(1));
			rent.setRentStatus(rentStatus);
			rents.add(rent);

			// 생성한 rent 저장
			this.insertRent(rent);
		}
		
		return rents;
	}
	
	// 반납 시 예약 삭제 및 rent 생성
	@Transactional
	private void deleteReservationAndInsertRent(List<Long> bookIds, List<Book> books, Timestamp rentDate) {
		// 반납할 도서의 예약 정보 가져오기
		List<Reservation> reservations = new ArrayList<>();
		reservations = reservationService.selectReservByBookIds(bookIds);
		if(reservations == null || reservations.isEmpty()) return;
		
		Map<Long, Reservation> reservationMap = reservations.stream()
			    .collect(Collectors.toMap(Reservation::getBookId, r -> r));
		List<Book> reservedBooks = books.stream()
			    .filter(book -> reservationMap.containsKey(book.getBookId()))
			    .toList();
		
		// 예약한 도서에 대한 대여 신청 생성하기
		for(Book book : reservedBooks) {
		    Reservation reservation = reservationMap.get(book.getBookId());

		    // 대여 중인 도서개수 추가
			book.setBookRentStock(book.getBookRentStock()+1); 

			// 예약 가능 시간
			Timestamp rentDueDate = new Timestamp(rentDate.getTime() + TimeUnit.DAYS.toMillis(RentProperties.RENT_DATE.getValue()));

			// rent 생성
			Rent rent = new Rent();
			rent.setBookId(book.getBookId());
			rent.setMemId(reservation.getMemId());
			rent.setRentDate(rentDate);
			rent.setRentDueDate(rentDueDate);
			rent.setRentStatus(RentStatus.REQUESTED);

			// 생성한 rent 저장
			this.insertRent(rent);
		}
		
		// 예약 삭제
		List<Long> reservedIds = reservations.stream()
			    .map(Reservation::getReservId)
			    .toList();
		reservationService.deleteReservs(reservedIds);
	}
	
	// 현재 시간과 반납 시간 계산
	private List<Timestamp> geCurrentTimestamps() {
		// 대여 시간과 반납 시간 설정
		List<Timestamp> dates = new ArrayList<>();
		Timestamp rentDate = new Timestamp(System.currentTimeMillis());
		Timestamp rentDueDate = new Timestamp(rentDate.getTime() + TimeUnit.DAYS.toMillis(RentProperties.RENT_DATE.getValue()));
		dates.add(rentDate);
		dates.add(rentDueDate);
		return dates;
	}

	//대여중인 데이터만 조회되도록
	public List<RentMemberResponse> findActiveRents() {
	    List<RentMemberResponse> rents = rentRepository.findActiveRents();
	    System.out.println("조회된 대여 데이터: " + rents);
	    return rents;
	}

	@Override
	public List<String> processOverdueBooks() {
	    List<RentMemberResponse> activeRents = rentRepository.findActiveRents();
	    List<String> overdueMessages = new ArrayList<>();
	    
	    for (RentMemberResponse rent : activeRents) {
	        int overdueDays = calculateDaysOverdue(rent.getRentDueDate());
	        
	        if (overdueDays > 0) {
	            String message = String.format(
	                "대여ID: %d, 회원명: %s, 전화번호: %s, 도서명: %s, 대여만기일: %s, 반납일: %s, 대여상태: %s, 연체일수: %d일",
	                rent.getRentId(),
	                rent.getMemName(),
	                rent.getMemPhone(),
	                rent.getBookTitle(),
	                rent.getRentDueDate(),
	                rent.getRentFinishDate(),
	                rent.getRentStatus(),
	                overdueDays
	            );
	            overdueMessages.add(message);
	        }
	    }
	    
	    return overdueMessages;
	}


	//연체일 계산
	private int calculateDaysOverdue(Timestamp rentDueDate) {
		if(rentDueDate == null) {
			return 0;
		}
		
        LocalDate dueDate = rentDueDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate today = LocalDate.now();
		int overdueDays = (int)ChronoUnit.DAYS.between(dueDate, today);
		
		return Math.max(overdueDays, 0);
	}
 }

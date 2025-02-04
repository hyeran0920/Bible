package com.library.bible.rent.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
import com.library.bible.rent.dto.RentPageResponse;
import com.library.bible.rent.model.Rent;
import com.library.bible.rent.model.RentStatus;
import com.library.bible.rent.repository.IRentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentService implements IRentService {
	private final IBookService bookService;
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
	
	// 책 ID로 rent 생성하기 -> 대여 신청하기 or 대여하기
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> insertRents(long memId, List<Long> bookIds, RentStatus rentStatus) {
		// 현재 사용자가 대여 중인 rents 조회
		List<Rent> currentRents = rentRepository.selectRentsByMemIdAndRentStatus(memId, rentStatus.toString());
		
		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);
		
		// 사용자가 대여 중인 도서 수 업데이트
		memberRent.setTotalRentCount(memberRent.getTotalRentCount() + bookIds.size());
		
		// rent 생성
		List<Rent> rents = new ArrayList<>();
		List<Book> books = new ArrayList<>();

		for(Long bookId : bookIds) {
			// 실제 존재하는 책인지 확인
			Book book = bookService.getBookInfo(bookId);

			// 현재 도서가 대여 가능한지 확인
			CheckRentAbout.checkRentBookPossible(book, memberRent, currentRents);

			book.setBookRentStock(book.getBookRentStock()+1); // 대여 중인 도서개수 추가
			books.add(book);

			// 시간
			Timestamp rentDate = new Timestamp(System.currentTimeMillis());
			Timestamp rentDueDate = new Timestamp(rentDate.getTime() + TimeUnit.DAYS.toMillis(7));

			// rent 생성
			Rent rent = new Rent();
			rent.setBookId(bookId);
			rent.setMemId(memId);
			rent.setRentDate(rentDate);
			rent.setRentDueDate(rentDueDate);
			rent.setRentStatus(rentStatus);
			rents.add(rent);

			// 생성한 rent 저장
			this.insertRent(rent);
		}
		
		// 대여 정보 변경사항 반영
		memberRentService.updateMemberRent(memberRent);

		// 도서 업데이트
		bookService.updateBookRentStocks(books);
		
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
	@CachePut(value = "rent", key = "#rentIds")
	public void updateRents(List<Rent> rents) {
		rents.stream().forEach(rent -> this.updateRent(rent));
	}
	
	// 대여 취소하기
	@Override
	@Transactional
	@CachePut(value = "rent", key = "#rentIds")
	public List<Rent> updateCancledRent(long memId, List<Long> rentIds) {
		// 취소할 대여 id값들이 없는 경우
		if(rentIds == null || rentIds.size() == 0)
			throw new CustomException(ExceptionCode.RENT_ID_NOT_INPUT);
		
		// 대여 내역을 취소 상태로 변경
		List<Rent> rents = rentRepository.selectRentsByRendIds(rentIds);
		// 대여 신청한 도서의 사용자가 아닌 경우
		if (rents.stream().anyMatch(rent -> rent.getMemId() != memId)) {
		    throw new CustomException(ExceptionCode.NOT_RENT_USER);
		}
		// 대여 신청한 도서가 아닌 경우
		if (rents.stream().anyMatch(rent -> rent.getRentStatus() != RentStatus.REQUESTED)) {
			throw new CustomException(ExceptionCode.NOT_RENT_REQUEST_CANCELD_STATE);
		}
		rents.stream().forEach(rent -> rent.setRentStatus(RentStatus.CANCLED));
		this.updateRents(rents);
		
		// 대여 신청한 도서 조회
		List<Book> books = bookService.getBookListByRentIds(rentIds);
		// book에 대여 중인 도서수 감소
		books.stream().forEach(book -> book.setBookRentStock(book.getBookRentStock() - 1));		
		// 도서 업데이트
		bookService.updateBookRentStocks(books);		

		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);		
		// 사용자가 대여 중인 도서 수 업데이트
		memberRent.setTotalRentCount(memberRent.getTotalRentCount() - books.size());
		// 대여 정보 변경사항 반영
		memberRentService.updateMemberRent(memberRent);

		return rents;
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
}

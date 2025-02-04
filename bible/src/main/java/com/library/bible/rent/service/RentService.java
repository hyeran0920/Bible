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
	
	// 책 ID로 rent 생성하기
	@Override
	@Transactional
	public List<Rent> insertRents(long memId, List<Long> bookIds, RentStatus rentStatus) {
		// 현재 사용자가 대여 중인 rents 조회
		List<Rent> currentRents = rentRepository.selectRentByMemIdAndRentStatus(memId, rentStatus.toString());
		
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
			book.setBookRentStock(book.getBookRentStock()+1); // 대여 중인 도서개수 추가
			books.add(book);
			
			// 현재 도서가 대여 가능한지 확인
			CheckRentAbout.checkRentBookPossible(book, memberRent, currentRents);

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

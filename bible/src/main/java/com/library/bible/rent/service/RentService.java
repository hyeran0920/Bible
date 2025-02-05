package com.library.bible.rent.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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
	
	// 책 ID로 rent 생성하기 -> 대여 신청하기
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> insertRequestRents(long memId, List<Long> bookIds, RentStatus rentStatus) {
		// 대여 신청할 id값들이 없는 경우
		if(isListNullOrEmpty(bookIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);
		
		// 현재 사용자가 대여 신청 또는 대여 중인 rents 조회
		List<Rent> currentRents = rentRepository.selectRentsByMemIdAndRentStatusList(
				memId, 
				List.of(RentStatus.REQUESTED.toString(), RentStatus.IN_USE.toString())
		);
		
		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);
		
		// 사용자가 대여 중인 도서 수 업데이트
		memberRent.setTotalRentCount(memberRent.getTotalRentCount() + bookIds.size());
		
		// 대여 신청할 도서 상태 변경 & 대여 생성
		List<Rent> rents = updateBookRentStatusAndcreateRent(bookIds, memberRent, currentRents, rentStatus);
		
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

		// 현재 사용자가 대여 신청 또는 대여 중인 rents 조회
		List<Rent> currentRents = rentRepository.selectRentsByMemIdAndRentStatusList(
				memId, 
				List.of(RentStatus.REQUESTED.toString(), RentStatus.IN_USE.toString())
		);
		
		// 현재 사용자의 대여 정보 확인
		MemberRent memberRent = memberRentService.selectMemberRentByMemId(memId);
		
		// 대여 신청하지 않은 도서
		List<Long> nonRequestBookIds = bookIds.stream()
			    .filter(bookId -> currentRents.stream().noneMatch(rent -> rent.getBookId() == bookId))
			    .collect(Collectors.toList());

		// 사용자가 대여 중인 도서 수 업데이트
		memberRent.setTotalRentCount(memberRent.getTotalRentCount() + nonRequestBookIds.size());
			
		// 대여 신청한 도서의 대여
		List<Rent> rents = currentRents.stream()
			    .filter(rent -> bookIds.contains(rent.getBookId()) && RentStatus.REQUESTED == rent.getRentStatus())
			    .collect(Collectors.toList());
		// 대여 신청 중인 도서를 대여 중으로 변경
		this.updateRentedRent(memId, rents);

		// 대여 신청 안한 도서의 상태 변경 & 대여 생성
		if(!isListNullOrEmpty(nonRequestBookIds))
			rents.addAll(updateBookRentStatusAndcreateRent(nonRequestBookIds, memberRent, currentRents, rentStatus));	
		
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
		rents.stream().forEach(rent -> this.updateRent(rent));
	}
	
	// 대여 취소하기
	@Override
	@Transactional
	@CachePut(value = "rent", key = "#rentIds")
	public List<Rent> updateCancledRent(long memId, List<Long> rentIds) {
		// 취소할 대여 id값들이 없는 경우
		if(isListNullOrEmpty(rentIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);
		
		// rentIds로 대여 중인 rent 조회하기
		List<Rent> rents = rentRepository.selectRentsByRendIds(rentIds);
		// 대여 신청한 도서의 사용자가 아닌 경우
		if (rents.stream().anyMatch(rent -> rent.getMemId() != memId)) {
		    throw new CustomException(ExceptionCode.NOT_RENT_USER);
		}
		// 대여 신청한 도서가 아닌 경우
		if (rents.stream().anyMatch(rent -> rent.getRentStatus() != RentStatus.REQUESTED)) {
			throw new CustomException(ExceptionCode.NOT_RENT_REQUEST_CANCELD_STATE);
		}
		// 대여 내역을 취소 상태로 변경
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
	
	// 대여 신청한 도서의 대여하기
	@Override
	@Transactional
	@CachePut(value = "rent", key = "#rentIds")
	public List<Rent> updateRentedRent(long memId, List<Rent> rents) {
		// 대여할 대여 id값들이 없는 경우
		if(rents == null || rents.isEmpty()) return rents;

		// 대여 신청한 도서의 사용자가 아닌 경우
		if (rents.stream().anyMatch(rent -> rent.getMemId() != memId)) {
		    throw new CustomException(ExceptionCode.NOT_RENT_USER);
		}
		// 대여 신청한 도서가 아닌 경우
		if (rents.stream().anyMatch(rent -> rent.getRentStatus() != RentStatus.REQUESTED)) {
			throw new CustomException(ExceptionCode.NOT_RENT_STATE);
		}

		// 대여 내역을 대여 상태로 변경
		rents.stream().forEach(rent -> rent.setRentStatus(RentStatus.IN_USE));
		this.updateRents(rents); // DB에 저장

		return rents;
	}	
	
	// 대여한 도서 반납하기
	@Override
	@Transactional
	@CacheEvict(value = "rents", allEntries = true)
	public List<Rent> updateReturnedRent(long memId, List<Long> bookIds) {
		// 취소할 대여 id값들이 없는 경우
		if(isListNullOrEmpty(bookIds))
			throw new CustomException(ExceptionCode.RENT_OR_BOOK_ID_NOT_INPUT);
		
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
		// 대여 내역을 취소 상태로 변경
		rents.stream().forEach(rent -> rent.setRentStatus(RentStatus.RETURNED));
		this.updateRents(rents);
		
		// 대여 신청한 도서 조회
		List<Book> books = bookService.getBookListByBookIds(bookIds);
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
	
	// list가 비어있는지 확인
	public boolean isListNullOrEmpty(List<Long> list) {
		if(list == null || list.isEmpty()) return true;
		return false;
	}
	
	// 도서 상태 변경 & 대여 생성 -> 대여 신청 또는 대여할 도서에만 해당
	public List<Rent> updateBookRentStatusAndcreateRent(List<Long> bookIds, MemberRent memberRent, List<Rent> currentRents, RentStatus rentStatus) {
		List<Rent> rents = new ArrayList<>();
		// 실제 존재하는 책인지 확인
		List<Book> books = bookService.getBookListByBookIds(bookIds);

		for(Book book : books) {
			// 현재 도서가 대여 가능한지 확인
			CheckRentAbout.checkRentBookPossible(book, memberRent, currentRents);
			book.setBookRentStock(book.getBookRentStock()+1); // 대여 중인 도서개수 추가

			// 시간
			Timestamp rentDate = new Timestamp(System.currentTimeMillis());
			Timestamp rentDueDate = new Timestamp(rentDate.getTime() + TimeUnit.DAYS.toMillis(7));

			// rent 생성
			Rent rent = new Rent();
			rent.setBookId(book.getBookId());
			rent.setMemId(memberRent.getMemId());
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
}

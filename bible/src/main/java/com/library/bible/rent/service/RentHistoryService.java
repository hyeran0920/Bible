package com.library.bible.rent.service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.library.bible.book.service.BookService;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.pageresponse.PageResponse;
import com.library.bible.rent.dto.RentHistoryResponse;
import com.library.bible.rent.mapper.RentHistoryMapper;
import com.library.bible.rent.model.Rent;
import com.library.bible.rent.model.RentHistory;
import com.library.bible.rent.model.RentStatus;
import com.library.bible.rent.repository.IRentHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentHistoryService implements IRentHistoryService {
	private final RentService rentService;
	private final BookService bookService;
	private final RentHistoryMapper rentHistoryMapper;
	private final IRentHistoryRepository rentHistoryRepository;

	@Override
	@Cacheable(value="rentHistorys")
	public List<RentHistory> selectAllRentHistory() {
		return rentHistoryRepository.selectAllRentHistory();
	}

	@Override
	@Cacheable(value="rentHistory", key="#rentHistoryId")
	public RentHistory selectRentHistory(long rentHistoryId) {
		RentHistory rentHistory = rentHistoryRepository.selectRentHistory((int)rentHistoryId);
		if(rentHistory == null) throw new CustomException(ExceptionCode.RENT_HISTORY_NOT_FOUND);
		return rentHistory;
	}

	@Override
	@Transactional
	@CacheEvict(value = "rentHistorys", allEntries = true)
	public void insertRentHistory(RentHistory rentHistory) {
		int result = rentHistoryRepository.insertRentHistory(rentHistory);
		if(result == 0) throw new CustomException(ExceptionCode.RENT_HISTORY_INSERT_FAIL);
	}
	
	@Override
	@Transactional
	public RentHistoryResponse insertRentHistoryAndRent(long memId , List<Integer> books, RentStatus rentStatus) {
		// 실제 존재하는 책인지 확인
		for(Integer bookId : books) {
//			bookService.getBookInfo(bookId);
		}
		
		// rent-history 생성
		RentHistory rentHistory = new RentHistory();
//		rentHistory.setMemId(memId);
		this.insertRentHistory(rentHistory);

		// rent 생성
		List<Rent> rents = new ArrayList<>();
		for(Integer bookId : books) {
			// 실제 존재하는 책인지 확인
//			bookService.getBookInfo(bookId);
			
			Rent rent = new Rent();
			rent.setBookId(bookId);
			rent.setRentHistoryId(rentHistory.getRentHistoryId());
			rent.setRentStatus(rentStatus);			
			rents.add(rent);
		}
		
		// 생성한 rent 저장
		rentService.insertRents(rents);
		
		return rentHistoryMapper.rentAndRentHistoryToRentHistoryResponse(rents, rentHistory);
	}

	@Override
	@Transactional
	@CachePut(value = "rentHistory", key = "#rentHistory.rentHistoryId")
	public void updateRentHistory(RentHistory rentHistory) {
		int result = rentHistoryRepository.updateRentHistory(rentHistory);
		if(result == 0) throw new CustomException(ExceptionCode.RENT_HISTORY_UPDATE_FAIL);
	}

	@Override
	@Transactional
	@Caching(evict= {
		@CacheEvict(value = "rentHistory", key = "#rentHistoryId"),
		@CacheEvict(value = "rentHistorys", allEntries = true)
	})
	public int deleteRentHistory(long rentHistoryId) {
		// rent-history 삭제 전 rent들 삭제
//		rentService.deleteRentByRentHistoryId(rentHistoryId);
		
		// rent-history 삭제
		int result = rentHistoryRepository.deleteRentHistory((int)rentHistoryId);
		if(result == 0) throw new CustomException(ExceptionCode.RENT_HISTORY_DELETE_FAIL);
		return result;
	}

	// RentHistoryRespone
	@Override
	@Transactional
    public PageResponse<RentHistoryResponse> selectRentHistoryResponses(long membId, Optional<RentStatus> rentStatus, PageRequest pageRequest) {
		String status = rentStatus
			    .map(RentStatus::toString) // 값이 있으면 toString() 호출
			    .orElse(null);  // 값이 없으면 기본값 반환
		
		List<RentHistoryResponse> content = rentHistoryRepository.selectRentHistoryResponses(
    		membId,
    		status,
            pageRequest.getPageNumber() * pageRequest.getPageSize(),
            pageRequest.getPageSize()
        );
        
        int total = rentHistoryRepository.countRentHistory(membId, status);

		int totalPages = (int) Math.ceil((double) total / pageRequest.getPageSize());
        boolean isLast = pageRequest.getPageNumber() >= totalPages - 1;

        return new PageResponse<RentHistoryResponse>(content, totalPages, total, isLast);
	}
}

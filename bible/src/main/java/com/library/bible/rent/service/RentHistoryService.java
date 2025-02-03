package com.library.bible.rent.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.rent.model.RentHistory;
import com.library.bible.rent.repository.IRentHistoryRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RentHistoryService implements IRentHistoryService {
	private final RentService rentService;
	private final IRentHistoryRepository rentHistoryRepository;

	@Override
	@Cacheable(value="rentHistorys")
	public List<RentHistory> selectAllRentHistory() {
		return rentHistoryRepository.selectAllRentHistory();
	}

	@Override
	@Cacheable(value="rentHistory", key="#rentHistoryId")
	public RentHistory selectRentHistory(int rentHistoryId) {
		RentHistory rentHistory = rentHistoryRepository.selectRentHistory(rentHistoryId);
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
	public int deleteRentHistory(int rentHistoryId) {
		// rent-history 삭제 전 rent들 삭제
		rentService.deleteRentByRentHistoryId(rentHistoryId);
		
		// rent-history 삭제
		int result = rentHistoryRepository.deleteRentHistory(rentHistoryId);
		if(result == 0) throw new CustomException(ExceptionCode.RENT_HISTORY_DELETE_FAIL);
		return result;
	}
}

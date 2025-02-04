package com.library.bible.rent.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;

import com.library.bible.pageresponse.PageResponse;
import com.library.bible.rent.dto.RentHistoryResponse;
import com.library.bible.rent.model.RentHistory;
import com.library.bible.rent.model.RentStatus;

public interface IRentHistoryService {
	List<RentHistory> selectAllRentHistory();
	
	RentHistory selectRentHistory(long rentHistoryId);
	void insertRentHistory(RentHistory rentHistory);
	void updateRentHistory(RentHistory rentHistory);
	int deleteRentHistory(long rentHistoryId);
	
	// RentHistoryRespone
	PageResponse<RentHistoryResponse> selectRentHistoryResponses(long memId, Optional<RentStatus> rentStatus, PageRequest pageRequest);
	RentHistoryResponse insertRentHistoryAndRent(long memId, List<Integer> books, RentStatus rentStatus);
}

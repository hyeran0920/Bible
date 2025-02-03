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
	
	RentHistory selectRentHistory(int rentHistoryId);
	void insertRentHistory(RentHistory rentHistory);
	void updateRentHistory(RentHistory rentHistory);
	int deleteRentHistory(int rentHistoryId);
	
	// RentHistoryRespone
	PageResponse<RentHistoryResponse> selectRentHistoryResponses(int memId, Optional<RentStatus> rentStatus, PageRequest pageRequest);
}

package com.library.bible.rent.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;

import com.library.bible.pageresponse.PageResponse;
import com.library.bible.rent.dto.RentPageResponse;
import com.library.bible.rent.model.Rent;
import com.library.bible.rent.model.RentStatus;

public interface IRentService {
	
	Rent selectRent(long rentId);
	List<Rent> selectAllRent();
	PageResponse<RentPageResponse> selectRentResponses(long memId, Optional<RentStatus> rentStatus, PageRequest pageRequest);	

	void insertRent(Rent rent);
	List<Rent> insertRents(List<Rent> rent);
	List<Rent> insertRents(long membId, List<Long> books, RentStatus rentStatus);

	void updateRent(Rent rent);
	void updateRents(List<Rent> rents);
	List<Rent> updateCancledRent(long memId, List<Long> rentIds); // 대여신청 취소하기

	int deleteRent(long rentId);
}

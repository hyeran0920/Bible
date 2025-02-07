package com.library.bible.rent.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;

import com.library.bible.book.model.Book;
import com.library.bible.pageresponse.PageResponse;
import com.library.bible.rent.dto.RentMemberResponse;
import com.library.bible.rent.dto.RentPageResponse;
import com.library.bible.rent.model.Rent;
import com.library.bible.rent.model.RentStatus;


public interface IRentService {
	
	Rent selectRent(long rentId);
	List<Rent> selectAllRent();
	List<Rent> selectRentsByRendIds(List<Long> rentIds);
	PageResponse<RentPageResponse> selectRentResponses(long memId, Optional<RentStatus> rentStatus, PageRequest pageRequest);	

	void insertRent(Rent rent);
	List<Rent> insertRents(List<Rent> rent);
	List<Rent> insertRequestRents(long membId, List<Long> bookIds, RentStatus rentStatus);
	List<Rent> insertAndUpdateRentalRents(long membId, List<Long> bookIds, RentStatus rentStatus); // 대여 신청 및 대여 신청 안한 도서 대여하기

	void updateRent(Rent rent);
	void updateRents(List<Rent> rents);
	List<Rent> updateCancledRent(long memId, List<Long> rentIds); 	// 대여신청 취소하기
	List<Rent> updateReturnedRent(long memId, List<Long> bookIds);	// 반납하기
	List<Rent> updateRenewalRent(long memId, List<Long> rentIds);	// 연장하기
	List<Rent> updateRentedReturnedRent(long memId, List<Long> bookIds);	// 대여 및 반납하기

	int deleteRent(long rentId);
	
	
	// 활성화된 대여 목록 조회
    List<RentMemberResponse> findActiveRents();
    // 연체일 계산
    List<String> processOverdueBooks();
    List<Book> getRentedBooks(long memId);
}

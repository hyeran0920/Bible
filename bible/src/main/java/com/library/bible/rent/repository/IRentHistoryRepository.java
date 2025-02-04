package com.library.bible.rent.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.rent.dto.RentHistoryResponse;
import com.library.bible.rent.model.RentHistory;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
@Repository
public interface IRentHistoryRepository {
	//Get all rent list
	List<RentHistory> selectAllRentHistory();
	
	//Rent table CRUD
	RentHistory selectRentHistory(int rentHistoryId);
	int insertRentHistory(RentHistory rentHistory);
	int updateRentHistory(RentHistory rentHistory);
	int deleteRentHistory(int rentHistoryId);
	
	// RentHistoryRespone
	List<RentHistoryResponse>selectRentHistoryResponses(
			@Param("memId") long memId, 
	        @Param("rentStatus") String rentStatus,
            @Param("offset") int offset, 
            @Param("limit") int limit
    );
	int countRentHistory(			
			@Param("memId") long memId, 
	        @Param("rentStatus") String rentStatus
    );
}

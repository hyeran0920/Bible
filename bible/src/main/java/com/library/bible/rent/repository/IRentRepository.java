package com.library.bible.rent.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.rent.dto.RentPageResponse;
import com.library.bible.rent.model.Rent;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
@Repository
public interface IRentRepository {
	
	//Get all rent list
	List<Rent> selectAllRent();
	List<Rent> selectRentByMemIdAndRentStatus(		
		@Param("memId") long memId, 
		@Param("rentStatus") String rentStatus);
	Rent selectRent(long rentId);
	
	//Rent table CRUD
	int insertRent(Rent rent);
	int updateRent(Rent rent);
	int deleteRent(long rentId);
	
	// RentResponse - pagenation
	List<RentPageResponse>selectRentResponses(
		@Param("memId") long memId, 
		@Param("rentStatus") String rentStatus,
		@Param("offset") int offset, 
		@Param("limit") int limit
	);
	int countRent(			
		@Param("memId") long memId, 
		@Param("rentStatus") String rentStatus
	);
}

package com.library.bible.rent.repository;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.hibernate.jdbc.Expectation.OutParameter;
import org.springframework.stereotype.Repository;

import com.library.bible.rent.dto.RentMemberResponse;
import com.library.bible.rent.dto.RentPageResponse;
import com.library.bible.rent.model.Rent;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
@Repository
public interface IRentRepository {
	
	//Get all rent list
	List<Rent> selectAllRent();
	List<Rent> selectRentsByRendIds(List<Long> rentIds);
	List<Rent> selectRentsByMemIdAndRentStatus(
		@Param("memId") long memId, 
		@Param("rentStatus") String rentStatus);
	List<Rent> selectRentsByMemIdAndRentStatusList(
		@Param("memId") long memId, 
		@Param("rentStatusList") List<String> rentStatusList);
	Rent selectRent(long rentId);
	
	//Rent table CRUD
	int insertRent(Rent rent);
	int updateRent(Rent rent);
	int updateRents(List<Rent> rents);
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
	
	List<RentMemberResponse> findActiveRents();
    List<String> processOverdueBooks();


}

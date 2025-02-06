package com.library.bible.reservation.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.reservation.model.Reservation;

import io.lettuce.core.dynamic.annotation.Param;

@Mapper
@Repository
public interface IReservationRepository {
	//Get all reservation list
	List<Reservation> selectAllReserv();
	List<Reservation> selectReservByMemId(long memId);
	List<Reservation> selectReservByBookIds(List<Long> bookIds);
	
	//Reservation CRUD
	Reservation selectReserv(long reservId);
	void insertReserv(Reservation reservation);
	void updateReserv(Reservation reservation);
	int deleteReserv(long reservId);
	int deleteReservs(List<Long> reservIds);
	int deleteReservsByMemId(@Param("reservIds") List<Long> reservIds, @Param("memId") Long memId);
}

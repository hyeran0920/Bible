package com.library.bible.reservation.service;

import java.util.List;

import com.library.bible.reservation.model.Reservation;

import io.lettuce.core.dynamic.annotation.Param;

public interface IReservationService {
	List<Reservation> selectAllReserv();
	List<Reservation> selectReservByMemId(long memId);
	List<Reservation> selectReservByBookIds(List<Long> bookIds);

	Reservation selectReserv(long reservId);
	void insertReserv(Reservation reservation);
	void updateReserv(Reservation reservation);
	int deleteReserv(long reservId);
	int deleteReservs(List<Long> reservIds);
	int deleteReservsByMemId(List<Long> reservIds, Long memId);
}

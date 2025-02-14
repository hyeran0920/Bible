package com.library.bible.reservation.service;

import java.util.List;

import com.library.bible.reservation.dto.ReservationResponse;
import com.library.bible.reservation.model.Reservation;

public interface IReservationService {
	List<ReservationResponse> selectReservResponsesByMemId(Long memId);
	List<Reservation> selectReservByBookIds(List<Long> bookIds);

	Reservation selectReserv(long reservId);
	Reservation insertReserv(Reservation reservation);
	List<Reservation> insertReservByBookIds(List<Long> bookIds, long memId);
	Reservation updateReserv(Reservation reservation);
	int deleteReserv(long reservId);
	int deleteReservs(List<Long> reservIds);
	int deleteReservsByMemId(List<Long> reservIds, Long memId);
}

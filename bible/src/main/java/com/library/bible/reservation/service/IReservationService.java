package com.library.bible.reservation.service;

import java.util.List;

import com.library.bible.reservation.model.Reservation;

public interface IReservationService {
	List<Reservation> selectAllReserv();
	
	Reservation selectReserv(int reservId);
	void insertReserv(Reservation reservation);
	void updateReserv(Reservation reservation);
	int deleteReserv(int reservId);
}

package com.library.bible.reservation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.reservation.model.Reservation;
import com.library.bible.reservation.repository.IReservationRepository;

import jakarta.transaction.Transactional;

@Service
public class ReservationService implements IReservationService {

	@Autowired
	private IReservationRepository reservRepository;
	
	@Override
	@Cacheable(value="reserv")
	public List<Reservation> selectAllReserv() {
		return reservRepository.selectAllReserv();
	}
	
	// 사용자 id로 예약들 조회하기
	@Override
	public List<Reservation> selectReservByMemId(long memId) {
		return reservRepository.selectReservByMemId(memId);
	}
	
	// 책 id들로 예약들 조회하기
	@Override
	public List<Reservation> selectReservByBookIds(List<Long> bookIds) {
		List<Reservation> reservs = reservRepository.selectReservByBookIds(bookIds);
		return reservs;
	}

	@Override
	@Cacheable(value="reserv", key="#reservId")
	public Reservation selectReserv(long reservId) {
		Reservation reservs = reservRepository.selectReserv(reservId);
		if(reservs == null)
			throw new CustomException(ExceptionCode.RESERVATION_NOT_FOUND);
		return reservs;
	}

	@Override
	@Transactional
	@CacheEvict(value="reservs", allEntries=true)
	public void insertReserv(Reservation reservation) {
		reservRepository.insertReserv(reservation);
	}

	@Override
	@Transactional
	@CachePut(value="reserv", key="#reservation.reservId")
	public void updateReserv(Reservation reservation) {
		reservRepository.updateReserv(reservation);
	}

	@Override
	@Transactional
	@Caching(evict= {
		@CacheEvict(value="reserv", key="#reservId"),
		@CacheEvict(value="reservs", allEntries=true)
	})
	public int deleteReserv(long reservId) {
		int result = reservRepository.deleteReserv(reservId);
		if(result == 0) throw new CustomException(ExceptionCode.RESERVATION_DELETE_FAIL);
		return result;
	}
	
	// reservIds를 이용해서 한 번에 예약 삭제
	@Override
	@Transactional
	@CacheEvict(value="reservs", allEntries=true)
	public int deleteReservs(List<Long> reservIds) {
		int result = reservRepository.deleteReservs(reservIds);
		if(result != reservIds.size()) throw new CustomException(ExceptionCode.RESERVATION_DELETE_FAIL);
		return result;
	}
	
	// 해당 사용자의 reservIds에 해당하는 예약 삭제
	@Override
	@Transactional
	public int deleteReservsByMemId(List<Long> reservIds, Long memId) {
		int result = reservRepository.deleteReservsByMemId(reservIds, memId);
		if(result != reservIds.size()) throw new CustomException(ExceptionCode.RESERVATION_DELETE_FAIL);
		return result;
	}

}

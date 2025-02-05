package com.library.bible.rent.config;

import java.time.LocalDate;
import java.util.List;

import com.library.bible.book.model.Book;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.memberrent.model.MemberRent;
import com.library.bible.rent.model.Rent;
import com.library.bible.reservation.model.Reservation;

public class CheckRentAbout {
	
	// 대여 신청 또는 대여 가능한 여부 확인
	public static boolean checkRentBookPossible(Book book, MemberRent memberRent, List<Rent> currentRents) {
		// 대여가능한 도서가 없을 경우
		if(book.getBookTotalStock() - book.getBookRentStock() <= 0) 
			throw new CustomException(ExceptionCode.NO_AVAILABLE_BOOK);

		// 연체 중이면 대여 신청 불가능
		if(memberRent.getRentPoss() == 'f') 
			throw new CustomException(ExceptionCode.OVERDUE_RENT);
		
		// 대여 가능한 개수 초과 시 대여 신청 불가능
		if(memberRent.getTotalRentCount() > RentProperties.POSSIBLE_BOOK_COUNT.getValue()) 
			throw new CustomException(ExceptionCode.EXCEEDED_RENT_LIMIT);
		
		// 대여 중인 도서이면 대여 신청불가능
		boolean exists = currentRents.stream().anyMatch(rent -> rent.getBookId() == book.getBookId());
		if(exists) throw new CustomException(ExceptionCode.ALREADY_RENTED);
		
		return true;
	}
	
	// 연장 가능한지 확인
	public static boolean checkRenewalBookPossible(Rent rent, List<Book> books, MemberRent memberRent, List<Reservation> reservations) {
		// 연체 중이면 연체 신청 불가능
		if(memberRent.getRentPoss() == 'f') 
			throw new CustomException(ExceptionCode.OVERDUE_RENT);
		
		// 대여 가능한 책이 없고 예약한 사람이 있으면 연장 불가능
		boolean isCurrentReservation = reservations.stream() // 예약 여부(true: 존재)
			    .anyMatch(reservation -> reservation.getMemId() == memberRent.getMemId());
		Book book = books.stream()
			    .filter(b -> b.getBookId() == rent.getBookId())
			    .findFirst()
			    .orElseThrow(() -> new CustomException(ExceptionCode.BOOK_NOT_FOUND));
		if(book.getBookTotalStock() - book.getBookRentStock() <= 0 && isCurrentReservation)
			throw new CustomException(ExceptionCode.EXTENSION_DENIED_DUE_TO_RESERVATION);
		
		// 마지막 연장인 경우 연장 불가능
		LocalDate lastRenewalDate = rent.getRentDate().toLocalDateTime().toLocalDate()
			    .plusDays(RentProperties.RENT_DATE.getValue() + 
			        (RentProperties.RENEWAL_COUNT.getValue() * RentProperties.RENEWAL_DATE.getValue()));
		LocalDate currentDueDate = rent.getRentDueDate().toLocalDateTime().toLocalDate(); // 현재 반납 예정일
		if (lastRenewalDate.compareTo(currentDueDate) < 0)
		    throw new CustomException(ExceptionCode.EXTENSION_LIMIT_EXCEEDED);
		
		return true;
	}
}

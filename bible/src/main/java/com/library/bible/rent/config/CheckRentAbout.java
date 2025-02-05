package com.library.bible.rent.config;

import java.util.List;

import com.library.bible.book.model.Book;
import com.library.bible.exception.CustomException;
import com.library.bible.exception.ExceptionCode;
import com.library.bible.memberrent.model.MemberRent;
import com.library.bible.rent.model.Rent;

public class CheckRentAbout {
	
	// 대여 신청 또는 대여 가능한 여부 확인
	public static boolean checkRentBookPossible(Book book, MemberRent memberRent, List<Rent> currentRents) {
		// 대여가능한 도서가 없을 경우
		if(book.getBookTotalStock() - book.getBookRentStock() <= 0) 
			throw new CustomException(ExceptionCode.NO_AVAILABLE_BOOK);

		// 연체 중이면 대여 신청 불가능
		if(memberRent.getRentPoss() == 'f') 
			throw new CustomException(ExceptionCode.OVERDUE_RENT);
		
		if(memberRent.getTotalRentCount() > RentProperties.POSSIBLE_BOOK_COUNT.getValue()) 
			throw new CustomException(ExceptionCode.EXCEEDED_RENT_LIMIT);
		
		// 대여 중인 도서이면 대여 신청불가능
		boolean exists = currentRents.stream().anyMatch(rent -> rent.getBookId() == book.getBookId());
		if(exists) throw new CustomException(ExceptionCode.ALREADY_RENTED);
		
		return true;
	}
}

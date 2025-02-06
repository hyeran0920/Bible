package com.library.bible.book.dto;

import com.library.bible.book.model.Book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookAndReservationInfo {
	private Long reservId;
	private Long reservMemId;
	private Book book;
}

package com.library.bible.reservation.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
	private int reservId;
	private long memId;
	private int bookId;
	private Date reservDate;
}

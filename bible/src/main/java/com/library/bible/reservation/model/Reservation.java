package com.library.bible.reservation.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
	private int reservId;
	private int memId;
	private int bookId;
	private Timestamp reservDate;
	private String reservStatus;
}

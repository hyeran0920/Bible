package com.library.bible.reservation.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
	private long reservId;
	private long memId;
	private long bookId;
	private Timestamp reservDate;
}

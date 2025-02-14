package com.library.bible.rent.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rent {
	private long rentId;
	private long bookId;
	private long memId;
	private Timestamp rentDate;
	private Timestamp rentDueDate;
	private Timestamp rentFinishDate;
	private RentStatus rentStatus;
}

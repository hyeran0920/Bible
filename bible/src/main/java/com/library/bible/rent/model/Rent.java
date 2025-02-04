package com.library.bible.rent.model;

import java.sql.Date;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rent {
	private int rentId;
	private int bookId;
	private int rentHistoryId;
	private Timestamp rentDueDate;
	private Timestamp rentFinishDate;
	private RentStatus rentStatus;
}

package com.library.bible.rent.model;

import java.sql.Date;

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
	private Date rentDueDate;
	private Date rentFinishDate;
	private String rentStatus;
	private Date rentDate;
}

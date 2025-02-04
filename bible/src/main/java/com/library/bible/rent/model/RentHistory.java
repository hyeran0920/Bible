package com.library.bible.rent.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentHistory {
	private int rentHistoryId;
	private int memId;
	private Timestamp rentDate;
}

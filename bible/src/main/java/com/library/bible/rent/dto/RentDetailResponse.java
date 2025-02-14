package com.library.bible.rent.dto;

import java.sql.Timestamp;

import com.library.bible.rent.model.RentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentDetailResponse {
    private int rentId;
    private int bookId;
	private long memId;
    private Timestamp rentDueDate;		// 반납 예정일
    private Timestamp rentFinishDate;	// 반납일
    private RentStatus rentStatus;
    
    private String bookTitle;  
    private String bookImgPath; 
}
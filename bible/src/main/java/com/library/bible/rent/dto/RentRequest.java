package com.library.bible.rent.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentRequest {
	private List<Long> bookIds; // 대여 신청 또는 대여할 도서
	private List<Long> rentIds;	// 대여 취소, 대여, 연장, 반납할 도서
}

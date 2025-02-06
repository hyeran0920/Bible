package com.library.bible.reservation.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequest {
	private List<Long> bookIds; 		// 예약할 도서
	private List<Long> reservIds;	// 예약 삭제
}

package com.library.bible.rent.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentPageResponse {
    private Timestamp rentDate; 		// 대여일
    private List<RentDetailResponse> rents;
}
package com.library.bible.rent.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentHistoryResponse {
    private int rentHistoryId;
    private int memId;
    private Timestamp rentDate;
    private List<RentResponse> rents;
}
package com.library.bible.rent.dto;

import java.sql.Date;
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
    private Date rentDate;
    private List<RentResponse> rents;
}
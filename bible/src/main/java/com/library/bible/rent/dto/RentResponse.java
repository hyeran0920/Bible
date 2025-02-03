package com.library.bible.rent.dto;

import java.sql.Date;

import com.library.bible.rent.model.RentStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentResponse {
    private int rentId;
    private int bookId;
    private Date rentDueDate;
    private Date rentFinishDate;
    private RentStatus rentStatus;
}
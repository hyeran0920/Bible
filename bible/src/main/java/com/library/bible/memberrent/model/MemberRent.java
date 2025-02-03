package com.library.bible.memberrent.model;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberRent {
    private int memId;
    private int totalRentCount;
    private char rentPoss;
    private Date rentPossDate;
}

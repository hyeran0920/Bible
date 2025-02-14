package com.library.bible.rent.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

import com.library.bible.rent.model.RentStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentMemberResponse {
	
    private Long rentId;       // 대여 ID
    private Long memId;
    private Timestamp rentDueDate;  // 대여 만료일
    private Timestamp rentFinishDate; // 반납 완료일 (반납 전이면 null)
    private RentStatus rentStatus; // 대여 상태 (IN_USE 등)
    
    private String memName;    // 회원 이름
    private String memPhone;   // 회원 전화번호
    
    private String bookTitle;  // 도서 제목
    

}
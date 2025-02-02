package com.library.bible.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
public class OrderHistory {
    private Long orderHistoryId;
    private Long memId;
    private Date orderHistoryDate;
    private Long totalPrice;
    private String receivedName;
    private String address;
}

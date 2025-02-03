package com.library.bible.order;

import java.util.Date;

import org.apache.ibatis.type.Alias;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Alias("OrderHistory")
public class OrderHistory {
    private Long orderHistoryId;
    private Long memId;
    private Date orderHistoryDate;
    private Long totalPrice;
    private String receivedName;
    private String address;
}

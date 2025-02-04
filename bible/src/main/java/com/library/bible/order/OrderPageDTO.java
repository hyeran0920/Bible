package com.library.bible.order;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPageDTO {
    private Long memId;
    private Long totalPrice;
    private String receivedName;
    private String address;
    private List<OrderPageItemDTO> orders;

    public OrderHistory toOrderHistory() {
        return OrderHistory.builder()
                .memId(memId)
                .totalPrice(totalPrice)
                .receivedName(receivedName)
                .address(address)
                .build();
    }
}

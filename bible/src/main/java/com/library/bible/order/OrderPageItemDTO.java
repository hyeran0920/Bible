package com.library.bible.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPageItemDTO {
    private Long bookId;
    private Long orderHistoryId;
    private Long sellingPrice;
    private Integer bookCount;
    private Long totalPrice;

    public void initSaleTotal() {
        this.totalPrice = this.sellingPrice * this.bookCount;
    }
}
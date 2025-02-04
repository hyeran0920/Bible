package com.library.bible.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPreviewDTO {
    private Integer cartId;
    private Integer bookId;
    private String bookTitle;
    private String bookAuthor;
    private Integer bookPrice;
    private Integer bookCount;
}

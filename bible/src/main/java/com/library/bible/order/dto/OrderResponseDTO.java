package com.library.bible.order.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponseDTO {
    private Long cartId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private int bookPrice;
    private int bookCount;

    public OrderResponseDTO(Long cartId, Long bookId, String bookTitle, String bookAuthor, int bookPrice, int bookCount) {
        this.cartId = cartId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPrice = bookPrice;
        this.bookCount = bookCount;
    }

}

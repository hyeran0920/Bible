package com.library.bible.book.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookReleaseDate;
    private String bookCategory;
    private long bookPrice;
    private String bookImgPath="";
    private String bookDetail;
    private long bookTotalStock;
    private long bookRentStock=0L;
    private String bookLocation;
    private String bookQrPath="";
}

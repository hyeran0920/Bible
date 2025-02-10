package com.library.bible.book.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Book {
    private long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookReleaseDate="";
    private String bookCategory;
    private long bookPrice;
    private String bookImgPath="";
    private String bookDetail;
    private long bookTotalStock;
    private long bookRentStock=0L;
    private String bookLocation;
    private String bookQrPath="";
}

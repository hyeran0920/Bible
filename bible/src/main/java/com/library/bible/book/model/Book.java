package com.library.bible.book.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Book {
    private int bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookPublisher;
    private String bookReleaseDate;
    private String bookCategory;
    private int bookPrice;
    private int bookStock;
    private String bookImg;
    private String bookDetail;
}

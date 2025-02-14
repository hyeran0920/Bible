package com.library.bible.cart.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Cart {
	private long cartId;
	private long bookId;
	private long memId;
	private int bookCount;
	
	//추가 order에 담기 위해
	private String bookTitle;
    private String bookAuthor;
    private int bookPrice;
}

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
}

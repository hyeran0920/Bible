package com.library.bible.cart.service;

import java.util.List;

import com.library.bible.cart.model.Cart;

public interface ICartService {
	//get cart info
	List<Cart> getAllCarts(long memId);
	Cart getCart(long cartId);
	int isBookInCart(long memId, long bookId);
	
	//update, delete, add
	void updateCart(long cartId, int newCount);
	void updateCartByBookId(long bookId, long memId, int bookCount);
	void deleteCart(long cartId);
	void addCart(long bookId, long memId, int bookCount);
	
	//calculate selected prices
	List<Long> getSelectedCartPrices(List<Long> cartId);
	long getSelectedTotalPrice(List<Long> cartId);
	
	
}

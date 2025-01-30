package com.library.bible.cart.service;

import java.util.List;

import com.library.bible.cart.model.Cart;

public interface ICartService {
	//get cart info
	List<Cart> getAllCarts(int memId);
	Cart getCart(int cartId);
	
	//update, delete, add
	void updateCart(int cartId, int newCount);
	void deleteCart(int cartId);
	void addCart(int bookId, int memId, int count);
	
	//calculate selected prices
	List<Integer> getSelectedCartPrices(List<Integer> cartId);
	int getSelectedTotalPrice(List<Integer> cartId);
	
}

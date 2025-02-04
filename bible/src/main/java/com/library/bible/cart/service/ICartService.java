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
	//구매 시 장바구니에서 삭제되는 deleteCarts
	void deleteCarts(List<Integer> cartIds);
	
	//calculate selected prices
	List<Long> getSelectedCartPrices(List<Long> cartId);
	long getSelectedTotalPrice(List<Long> cartId);
	List<Cart> getSelectedCarts(List<Integer> cartIds);
}

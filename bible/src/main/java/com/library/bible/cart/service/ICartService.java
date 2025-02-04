package com.library.bible.cart.service;

import java.util.List;

import com.library.bible.cart.model.Cart;

public interface ICartService {
	//get cart info
	List<Cart> getAllCarts(int memId);
	Cart getCart(int cartId);
	int isBookInCart(int memId, int bookId);
	
	//update, delete, add
	void updateCart(int cartId, int newCount);
	void updateCartByBookId(int bookId, int memId, int bookCount);
	void deleteCart(int cartId);
	void addCart(int bookId, int memId, int bookCount);
	//구매 시 장바구니에서 삭제되는 deleteCarts
	void deleteCarts(List<Integer> cartIds);
	
	//calculate selected prices
	List<Integer> getSelectedCartPrices(List<Integer> cartId);
	int getSelectedTotalPrice(List<Integer> cartId);
	List<Cart> getSelectedCarts(List<Integer> cartIds);
}

package com.library.bible.cart.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.bible.cart.model.Cart;
import com.library.bible.cart.repository.ICartRepository;

@Service
public class CartService implements ICartService{

	@Autowired
	private ICartRepository cartRepos;
	
	//GET CART
	@Override
	public List<Cart> getAllCarts(long memId) {
		return cartRepos.getAllCarts(memId);
	}

	@Override
	public Cart getCart(long cartId) {
		return cartRepos.getCart(cartId);
	}
	
	@Override
	public int isBookInCart(long memId, long bookId) {
	    return cartRepos.isBookInCart(memId,bookId);
	}

	
	//UPDATE, INSERT, DELETE
	@Override
	public void updateCart(long cartId, int newCount) {
		cartRepos.updateCart(cartId, newCount);
	}
	@Override
	public void updateCartByBookId(long bookId, long memId, int bookCount) {
		cartRepos.updateCartByBookId(bookId, memId, bookCount);
		
	}
	
	
	@Override
	public void deleteCart(long cartId) {
		cartRepos.deleteCart(cartId);
	}

	@Override
	public void addCart(long bookId, long memId,int bookCount) {
		cartRepos.addCart(bookId,memId, bookCount);
	}
	
	//GET PRICES
	@Override
	public List<Long> getSelectedCartPrices(List<Long> cartId) {
		return cartRepos.getSelectedCartPrices(cartId);
	}

	@Override
	public long getSelectedTotalPrice(List<Long> cartId) {
	    int sum = 0;
	    List<Long> prices = cartRepos.getSelectedCartPrices(cartId);

	    if (prices != null) {
	        for (long price : prices) {
	            sum += price;
	        }
	    }
	    return sum;
	}

	

	


}

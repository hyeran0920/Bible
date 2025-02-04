package com.library.bible.cart.service;

import java.util.Collections;
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

	// *선택한 장바구니 아이템 조회*
	@Override
	public List<Cart> getSelectedCarts(List<Integer> cartIds) {
	    if (cartIds == null || cartIds.isEmpty()) {
	        return Collections.emptyList();
	    }
	    return cartRepos.getSelectedCarts(cartIds);
	}
	//구매 시 장바구니에서 삭제
	@Override
	public void deleteCarts(List<Integer> cartIds) {
	    if (cartIds != null && !cartIds.isEmpty()) {
	        cartRepos.deleteCarts(cartIds);
	    }
	}
	

}

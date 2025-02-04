package com.library.bible.cart.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.library.bible.cart.model.Cart;

@Mapper
@Repository
public interface ICartRepository {
	//get cart info
	List<Cart> getAllCarts(int memId);
	Cart getCart(int cartId);
	int isBookInCart(int memId, int bookId);
	
	//update, delete, add
	void updateCart(int cartId, int newCount);
	void updateCartByBookId(int bookId, int memId, int bookCount);
	void deleteCart(int cartId);
	void addCart(int bookId,int memId, int bookCount);
	
	//calculate selected prices
	List<Integer> getSelectedCartPrices(List<Integer> cartId);
	int getSelectedTotalPrice(List<Integer> cartId);

    // **🔹 추가된 메서드**
    List<Cart> getSelectedCarts(List<Integer> cartIds);
    
 // **🔹 주문 완료 후 선택한 장바구니 항목 삭제**
    void deleteCarts(@Param("cartIds") List<Integer> cartIds);
}

package com.library.bible.cart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.library.bible.cart.model.Cart;
import com.library.bible.cart.repository.ICartRepository;

@Disabled
class CartServiceTest {

    @Mock
    private ICartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(ICartRepository.class);
        cartService = new CartService();
    }

    @Test
    void testGetAllCarts() {
        long memId = 1L;
        
        Cart cart1 = new Cart(); // 기본 생성자 사용
        cart1.setCartId(1L);
        cart1.setMemId(1L);
        cart1.setBookId(2L);
        cart1.setBookCount(3);

        List<Cart> mockCartList = Arrays.asList(cart1); // 생성자 문제 해결

        when(cartRepository.getAllCarts(memId)).thenReturn(mockCartList);

        List<Cart> result = cartService.getAllCarts(memId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCartId());
    }

    @Test
    void testAddCart() {
        long bookId = 1L;
        long memId = 1L;
        int bookCount = 2;

        doNothing().when(cartRepository).addCart(bookId, memId, bookCount);

        cartService.addCart(bookId, memId, bookCount);

        verify(cartRepository, times(1)).addCart(bookId, memId, bookCount);
    }

    @Test
    void testDeleteCart() {
        long cartId = 1L;

        doNothing().when(cartRepository).deleteCart(cartId);

        cartService.deleteCart(cartId);

        verify(cartRepository, times(1)).deleteCart(cartId);
    }
}

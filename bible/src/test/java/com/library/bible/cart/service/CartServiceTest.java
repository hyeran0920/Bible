package com.library.bible.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.library.bible.cart.model.Cart;
import com.library.bible.cart.repository.ICartRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private ICartRepository cartRepository;

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setCartId(1L);
        cart.setBookId(101L);
        cart.setMemId(201L);
        cart.setBookCount(2);
    }

    @Test
    @DisplayName("회원의 장바구니 목록 조회 테스트")
    void testGetAllCarts() {
        when(cartRepository.getAllCarts(201L)).thenReturn(Arrays.asList(cart));

        List<Cart> result = cartService.getAllCarts(201L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cart.getCartId(), result.get(0).getCartId());
    }

    @Test
    @DisplayName("특정 장바구니 항목 조회 테스트")
    void testGetCart() {
        when(cartRepository.getCart(1L)).thenReturn(cart);

        Cart result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(cart.getCartId(), result.getCartId());
    }

    @Test
    @DisplayName("책이 장바구니에 존재하는지 확인 테스트")
    void testIsBookInCart() {
        when(cartRepository.isBookInCart(201L, 101L)).thenReturn(1);

        int result = cartService.isBookInCart(201L, 101L);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("장바구니 수량 업데이트 테스트")
    void testUpdateCart() {
        doNothing().when(cartRepository).updateCart(1L, 3);

        cartService.updateCart(1L, 3);

        verify(cartRepository, times(1)).updateCart(1L, 3);
    }

    @Test
    @DisplayName("책 ID로 장바구니 업데이트 테스트")
    void testUpdateCartByBookId() {
        doNothing().when(cartRepository).updateCartByBookId(101L, 201L, 5);

        cartService.updateCartByBookId(101L, 201L, 5);

        verify(cartRepository, times(1)).updateCartByBookId(101L, 201L, 5);
    }

    @Test
    @DisplayName("장바구니 항목 삭제 테스트")
    void testDeleteCart() {
        doNothing().when(cartRepository).deleteCart(1L);

        cartService.deleteCart(1L);

        verify(cartRepository, times(1)).deleteCart(1L);
    }

    @Test
    @DisplayName("장바구니에 책 추가 테스트")
    void testAddCart() {
        doNothing().when(cartRepository).addCart(101L, 201L, 2);

        cartService.addCart(101L, 201L, 2);

        verify(cartRepository, times(1)).addCart(101L, 201L, 2);
    }

    @Test
    @DisplayName("선택한 장바구니 항목 가격 조회 테스트")
    void testGetSelectedCartPrices() {
        List<Long> cartIds = Arrays.asList(1L, 2L);
        when(cartRepository.getSelectedCartPrices(cartIds)).thenReturn(Arrays.asList(100L, 200L));

        List<Long> result = cartService.getSelectedCartPrices(cartIds);

        assertEquals(2, result.size());
        assertEquals(100L, result.get(0));
        assertEquals(200L, result.get(1));
    }

    @Test
    @DisplayName("선택한 장바구니 총 가격 조회 테스트")
    void testGetSelectedTotalPrice() {
        List<Long> cartIds = Arrays.asList(1L, 2L);
        when(cartRepository.getSelectedCartPrices(cartIds)).thenReturn(Arrays.asList(100L, 200L));

        long result = cartService.getSelectedTotalPrice(cartIds);

        assertEquals(300L, result);
    }

    @Test
    @DisplayName("선택한 장바구니 항목 조회 테스트")
    void testGetSelectedCarts() {
        List<Integer> cartIds = Arrays.asList(1, 2);
        when(cartRepository.getSelectedCarts(cartIds)).thenReturn(Arrays.asList(cart));

        List<Cart> result = cartService.getSelectedCarts(cartIds);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("구매 후 장바구니에서 항목 삭제 테스트")
    void testDeleteCarts() {
        List<Integer> cartIds = Arrays.asList(1, 2);
        doNothing().when(cartRepository).deleteCarts(cartIds);

        cartService.deleteCarts(cartIds);

        verify(cartRepository, times(1)).deleteCarts(cartIds);
    }
}

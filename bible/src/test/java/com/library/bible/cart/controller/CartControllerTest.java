package com.library.bible.cart.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.library.bible.cart.model.Cart;
import com.library.bible.cart.service.ICartService;
@Disabled
class CartControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICartService cartService;

    @InjectMocks
    private CartController cartController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }
    @Disabled
    @Test
    void testGetAllCarts() {
        long memId = 1L;
        
        Cart cart1 = new Cart(); // 기본 생성자 사용
        cart1.setCartId(1L);
        cart1.setMemId(1L);
        cart1.setBookId(2L);
        cart1.setBookCount(3);

        List<Cart> mockCartList = Arrays.asList(cart1); // 생성자 문제 해결

        //when(cartRepository.getAllCarts(memId)).thenReturn(mockCartList);

        List<Cart> result = cartService.getAllCarts(memId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getCartId());
    }


    @Test
    void testDeleteCart() throws Exception {
        long cartId = 1L;
        doNothing().when(cartService).deleteCart(cartId);

        mockMvc.perform(delete("/cart/{cartId}", cartId))
                .andExpect(status().isOk());

        verify(cartService, times(1)).deleteCart(cartId);
    }

    @Test
    void testAddCart() throws Exception {
        long bookId = 1L;
        long memId = 1L;
        int bookCount = 2;

        doNothing().when(cartService).addCart(bookId, memId, bookCount);

        mockMvc.perform(post("/cart/add")
                .param("bookId", String.valueOf(bookId))
                .param("memId", String.valueOf(memId))
                .param("bookCount", String.valueOf(bookCount))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cartService, times(1)).addCart(bookId, memId, bookCount);
    }
}

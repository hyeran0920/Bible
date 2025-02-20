package com.library.bible.cart.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.bible.cart.Mapper.CartMapper;
import com.library.bible.cart.dto.CartRequest;
import com.library.bible.cart.model.Cart;
import com.library.bible.cart.service.ICartService;
@Disabled
@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 비활성화
@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICartService cartService;

    @MockBean
    private CartMapper cartMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Cart cart;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this); // Mock 초기화

        cart = new Cart();
        cart.setCartId(1L);
        cart.setBookId(101L);
        cart.setMemId(201L);
        cart.setBookCount(2);
    }
    @Disabled
    @Test
    @DisplayName("장바구니 목록 조회 API 테스트")
    void testGetAllCarts() throws Exception {
        when(cartService.getAllCarts(anyLong())).thenReturn(Arrays.asList(cart));

        mockMvc.perform(get("/api/carts/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
    @Disabled
    @Test
    @DisplayName("장바구니 항목 추가 API 테스트")
    void testAddCart() throws Exception {
        CartRequest request = new CartRequest(101L, 2);
        doNothing().when(cartService).addCart(anyLong(), anyLong(), anyInt());

        mockMvc.perform(post("/api/carts/201")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}

package com.library.bible.order.controller;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.book.model.Book;
import com.library.bible.book.service.IBookService;
import com.library.bible.cart.model.Cart;
import com.library.bible.cart.service.ICartService;
import com.library.bible.member.model.Member;
import com.library.bible.order.OrderPageDTO;
import com.library.bible.order.OrderPageItemDTO;
import com.library.bible.order.service.OrderService;
import com.library.bible.resolver.AuthMember;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final ICartService cartService;
    private final IBookService bookService;

    public OrderController(OrderService orderService, ICartService cartService, IBookService bookService) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.bookService = bookService;
    }

    /**
     * 선택한 장바구니 상품을 주문하는 API
     */
    @PostMapping("/orders")
    public ResponseEntity<String> placeOrderFromCart(@AuthMember Member member, @RequestBody List<Integer> cartIds) {
        Long memId = Long.valueOf(member.getMemId()); // 현재 로그인한 사용자 ID
        
        // 선택한 장바구니 아이템을 가져오기
        List<Cart> selectedCarts = cartService.getSelectedCarts(cartIds);
        if (selectedCarts.isEmpty()) {
            return ResponseEntity.badRequest().body("선택한 장바구니 상품이 없습니다.");
        }
        
        // 리스트 내부 요소가 null인지 확인 후 필터링
        selectedCarts = selectedCarts.stream().filter(cart -> cart != null).collect(Collectors.toList());
        if (selectedCarts.isEmpty()) {
            return ResponseEntity.badRequest().body("잘못된 장바구니 정보가 포함되어 있습니다.");
        }

        // 장바구니 데이터를 주문 DTO로 변환
        OrderPageDTO orderPage = new OrderPageDTO();
        orderPage.setMemId(memId);
        long totalPrice = selectedCarts.stream()
                .mapToLong(cart -> {
                	Book book = bookService.getBookInfo(cart.getBookId());
                	return cart.getBookCount() * book.getBookPrice();		
                })
                .sum();
            
        orderPage.setTotalPrice(totalPrice);        
        orderPage.setReceivedName(member.getMemName()); // 회원 정보에서 기본값 설정
        orderPage.setAddress("회원 주소 정보"); // 추후 업데이트 필요

        // 주문 아이템 목록 생성
        List<OrderPageItemDTO> orderItems = selectedCarts.stream().map(cart -> {
        	if (cart == null) {
                return null; // null 값 처리
            }
            OrderPageItemDTO orderItem = new OrderPageItemDTO();
            Book book = bookService.getBookInfo(cart.getBookId());
            orderItem.setBookId(Long.valueOf(cart.getBookId()));
            orderItem.setBookCount(cart.getBookCount());
            orderItem.setSellingPrice(Long.valueOf(book.getBookPrice()));
            return orderItem;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        orderPage.setOrders(orderItems);

        // 주문 실행
        orderService.processOrder(Long.valueOf(member.getMemId()), orderPage);

        // 주문 완료 후 장바구니에서 삭제
        cartService.deleteCarts(cartIds);

        return ResponseEntity.ok("주문이 성공적으로 완료되었습니다.");
    }
}

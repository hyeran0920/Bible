package com.library.bible.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.order.OrderPageDTO;
import com.library.bible.order.OrderPageItemDTO;
import com.library.bible.order.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 주문 요청
    @PostMapping("/{memId}")
    public ResponseEntity<String> placeOrder(@PathVariable("memId") String memId, @RequestBody OrderPageDTO orderPage) {
        for (OrderPageItemDTO order : orderPage.getOrders()) {
            orderService.processOrder(order);
        }
        return ResponseEntity.ok("주문 성공!");
    }

    // 특정 회원의 주문 내역 조회
    @GetMapping("/{memId}")
    public ResponseEntity<List<OrderPageItemDTO>> getOrders(@PathVariable("memId") String memId) {
        List<OrderPageItemDTO> orders = orderService.getOrdersByMemId(memId);
        return ResponseEntity.ok(orders);
    }
}
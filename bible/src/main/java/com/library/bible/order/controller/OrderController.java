package com.library.bible.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.library.bible.order.OrderPageDTO;
import com.library.bible.order.OrderPageItemDTO;
import com.library.bible.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place")
    public Long placeOrder(@RequestBody OrderPageDTO orderPage) {
        return orderService.placeOrder(orderPage.toOrderHistory());
    }

    @GetMapping("/history/{memId}")
    public List<OrderPageItemDTO> getOrdersByMemId(@PathVariable Long memId) {
        return orderService.getOrdersByMemId(memId);
    }
}

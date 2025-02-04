package com.library.bible.order.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.order.dto.OrderPreviewDTO;
import com.library.bible.order.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * 주문 미리보기 API
     */
    @GetMapping("/preview")
    public List<OrderPreviewDTO> getOrderPreview(@RequestParam("cartIds") List<Integer> cartIds) {
        return orderService.getOrderPreview(cartIds);
    }

    /**
     * 주문 확정 API
     */
    @PostMapping("/confirm")
    public String confirmOrder(@RequestBody Map<String, List<Integer>> requestBody) {
        List<Integer> cartIds = requestBody.get("cartIds");
        orderService.confirmOrder(cartIds);
        return "주문이 완료되었습니다.";
    }
}


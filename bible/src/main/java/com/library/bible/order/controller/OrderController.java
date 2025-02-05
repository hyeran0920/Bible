package com.library.bible.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.bible.member.model.Member;
import com.library.bible.order.model.Order;
import com.library.bible.order.service.IOrderService;
import com.library.bible.resolver.AuthMember;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private IOrderService orderService; // Fixed typo from orderSerivce to orderService

    // GET-----------------------------------------------------------

    // Get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = orderService.getAllOrder();
        return ResponseEntity.ok(orders);
    }

    // Get orders - member
    @GetMapping("/me")
    public ResponseEntity<List<Order>> getOrders(@AuthMember Member member) {
    	List<Order> orders = orderService.getOrderByMemId(member.getMemId());
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/member/{memId}")
    public ResponseEntity<List<Order>> getMemberOrders(
    		@PathVariable("memId") @Positive long memId) {
    	List<Order> orders = orderService.getOrderByMemId(memId);
        return ResponseEntity.ok(orders);
    }

    // Get orders - order history ID
    @GetMapping("/history/{orderHistoryId}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable("orderHistoryId") @Positive long orderHistoryId) {
        List<Order> orders = orderService.getOrderByHistoryId(orderHistoryId);
        return ResponseEntity.ok(orders);
    }

    // Get a specific order
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable("orderId") @Positive long orderId) {
        Order order = orderService.getOrderById(orderId);
        return order != null ? ResponseEntity.ok(order) : ResponseEntity.notFound().build();
    }

    // Get total order count
    @GetMapping("/count")
    public ResponseEntity<Integer> getOrderCount() {
        int count = orderService.countAllOrders();
        return ResponseEntity.ok(count);
    }

    // Get order count - member
    @GetMapping("/count/me")
    public ResponseEntity<Integer> getOrderCount(@AuthMember Member member) {
        int count = orderService.countMemOrders(member.getMemId());
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/count/{memId}")
    public ResponseEntity<Integer> getMemberOrderCount(
    		@PathVariable("memId") @Positive long memId) {
        int count = orderService.countMemOrders(memId);
        return ResponseEntity.ok(count);
    }

    // INSERT---------------------------------------------------------

    // Insert a new order
    @PostMapping
    public ResponseEntity<Void> insertOrder(@RequestBody @Valid Order order) {
        orderService.insertOrder(order);
        return ResponseEntity.ok().build();
    }

    // DELETE---------------------------------------------------------

    // Delete an order - ID
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable("orderId") @Positive long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok().build();
    }

    // UPDATE---------------------------------------------------------

    // Update an existing order
    @PutMapping
    public ResponseEntity<Void> updateOrder(@RequestBody @Valid Order order) {
        orderService.updateOrder(order);
        return ResponseEntity.ok().build();
    }
}

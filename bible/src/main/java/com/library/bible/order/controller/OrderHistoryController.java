package com.library.bible.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.library.bible.member.model.Member;
import com.library.bible.order.model.OrderHistory;
import com.library.bible.order.service.IOrderHistoryService;
import com.library.bible.resolver.AuthMember;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/orderhistory")
public class OrderHistoryController {
    
    @Autowired
    private IOrderHistoryService orderHistoryService;

    // GET -----------------------------------------------------------

    // Get all order histories
    @GetMapping
    public ResponseEntity<List<OrderHistory>> getOrderHistory() {
        List<OrderHistory> orderHistories = orderHistoryService.getAllOrderHistory();
        return ResponseEntity.ok(orderHistories);
    }

    // Get order histories by authenticated member
    @GetMapping("/me")
    public ResponseEntity<List<OrderHistory>> getOrderHistory(@AuthMember Member member) {
        List<OrderHistory> orderHistories = orderHistoryService.getOrderHistoryByMemId(member.getMemId());
        return ResponseEntity.ok(orderHistories);
    }
    
    @GetMapping("/member/{memId}")
    public ResponseEntity<List<OrderHistory>> getMemberOrderHistory(
    		@PathVariable("memId") @Positive long memId) {
        List<OrderHistory> orderHistories = orderHistoryService.getOrderHistoryByMemId(memId);
        return ResponseEntity.ok(orderHistories);
    }
    

    // Get a specific order history by ID
    @GetMapping("/{orderHistoryId}")
    public ResponseEntity<OrderHistory> getOrderHistory(
    		@PathVariable("orderHistoryId") @Positive long orderHistoryId) {
        OrderHistory orderHistory = orderHistoryService.getOrderHistoryById(orderHistoryId);
        return orderHistory != null ? ResponseEntity.ok(orderHistory) : ResponseEntity.notFound().build();
    }

    // Get total count of order histories
    @GetMapping("/count")
    public ResponseEntity<Integer> getOrderHistoryCount() {
        int count = orderHistoryService.countOrderHistory();
        return ResponseEntity.ok(count);
    }

    // INSERT ---------------------------------------------------------

    // Insert a new order history
    @PostMapping
    public ResponseEntity<Void> insertOrderHistory(
    		@RequestBody @Valid OrderHistory orderHistory, 
    		@AuthMember Member member) 
    {
    	orderHistory.setMemId(member.getMemId());
        orderHistoryService.insertOrderHistory(orderHistory);
        return ResponseEntity.ok().build();
    }

    // DELETE ---------------------------------------------------------

    // Delete an order history by ID
    @DeleteMapping("/{orderHistoryId}")
    public ResponseEntity<Void> deleteOrderHistory(
    		@PathVariable("orderHistoryId") @Positive long orderHistoryId) {
        orderHistoryService.deleteOrderHistory(orderHistoryId);
        return ResponseEntity.ok().build();
    }

    // UPDATE ---------------------------------------------------------

    // Update an order history
    @PutMapping
    public ResponseEntity<Void> updateOrderHistory(@RequestBody @Valid OrderHistory orderHistory) {
        System.out.println("update order history="+orderHistory.getOrderHistoryId());
    	orderHistoryService.updateOrderHistory(orderHistory);
        return ResponseEntity.ok().build();
    }
}

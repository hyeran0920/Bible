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

import com.library.bible.book.model.Book;
import com.library.bible.book.service.BookService;
import com.library.bible.order.OrderPageDTO;
import com.library.bible.order.OrderPageItemDTO;
import com.library.bible.order.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private BookService bookService;

    @PostMapping("/place")
    public Long placeOrder(@RequestBody OrderPageDTO orderPage) {
        return orderService.placeOrder(orderPage.toOrderHistory());
    }

    @GetMapping("/history/{memId}")
    public List<OrderPageItemDTO> getOrdersByMemId(@PathVariable Long memId) {
        return orderService.getOrdersByMemId(memId);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> books = bookService.getBookList();
        return ResponseEntity.ok(books);
    }
    
}

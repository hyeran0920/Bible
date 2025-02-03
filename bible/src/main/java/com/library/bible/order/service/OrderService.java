package com.library.bible.order.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.bible.order.OrderHistory;
import com.library.bible.order.OrderPageDTO;
import com.library.bible.order.OrderPageItemDTO;
import com.library.bible.order.mapper.OrderMapper;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    
    @Transactional
    public Long placeOrder(OrderHistory orderHistory) {
        orderMapper.insertOrderHistory(orderHistory);
        return orderHistory.getOrderHistoryId();
    }

    @Transactional
    public void processOrder(Long memId, OrderPageDTO orderPage) {
        OrderHistory orderHistory = OrderHistory.builder()
            .memId(memId)
            .orderHistoryDate(new Date())
            .totalPrice(orderPage.getTotalPrice())
            .receivedName(orderPage.getReceivedName())
            .address(orderPage.getAddress())
            .build();

        orderMapper.insertOrderHistory(orderHistory);

        if (orderHistory.getOrderHistoryId() == null) {
            throw new RuntimeException("Order history ID was not generated.");
        }

        for (OrderPageItemDTO order : orderPage.getOrders()) {
            order.initSaleTotal();
            order.setOrderHistoryId(orderHistory.getOrderHistoryId());
            orderMapper.insertOrder(order);
        }
    }

    public List<OrderPageItemDTO> getOrdersByMemId(Long memId) { 
        return orderMapper.getOrdersByMemId(memId);
    }
    
}

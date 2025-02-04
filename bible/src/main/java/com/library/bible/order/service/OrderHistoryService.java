package com.library.bible.order.service;

import org.springframework.stereotype.Service;

import com.library.bible.order.mapper.OrderHistoryMapper;

import jakarta.transaction.Transactional;

@Service
public class OrderHistoryService {

    private final OrderHistoryMapper orderHistoryMapper;

    public OrderHistoryService(OrderHistoryMapper orderHistoryMapper) {
        this.orderHistoryMapper = orderHistoryMapper;
    }

    /**
     * 주문 내역을 생성하고 order_history_id를 반환
     */
    @Transactional
    public Long createOrderHistory(Long memId, Long addressId, String receivedName, String paymentMethod) {
        orderHistoryMapper.insertOrderHistory(memId, addressId, receivedName, paymentMethod);
        return orderHistoryMapper.getLastInsertId();
    }
}
package com.library.bible.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.bible.order.OrderPageItemDTO;
import com.library.bible.order.mapper.OrderMapper;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    // 주문 삽입
    public void processOrder(OrderPageItemDTO order) {
        order.initSaleTotal();  // 총 가격 계산
        orderMapper.insertOrder(order);  // MyBatis 실행
    }

    // 특정 회원의 주문 내역 조회
    public List<OrderPageItemDTO> getOrdersByMemId(String memId) {
        return orderMapper.getOrdersByMemId(memId);
    }
}

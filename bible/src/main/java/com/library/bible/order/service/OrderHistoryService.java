package com.library.bible.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.bible.order.model.OrderHistory;
import com.library.bible.order.repository.IOrderHistoryRepository;

@Service
public class OrderHistoryService implements IOrderHistoryService {

    @Autowired
    private IOrderHistoryRepository orderHisRepos; // Fixed variable naming for consistency

    @Override
    public List<OrderHistory> getAllOrderHistory() {
        return orderHisRepos.getAllOrderHistory();
    }

    @Override
    public List<OrderHistory> getOrderHistoryByMemId(long memId) {
        return orderHisRepos.getOrderHistoryByMemId(memId);
    }

    @Override
    public OrderHistory getOrderHistoryById(long orderHistoryId) { // Fixed return type to match expected behavior
        return orderHisRepos.getOrderHistoryById(orderHistoryId);
    }

    @Override
    public long insertOrderHistory(OrderHistory orderHistory) {
        orderHisRepos.insertOrderHistory(orderHistory);
        long orderHisId=orderHistory.getOrderHistoryId();
        System.out.println("order history id="+orderHisId);
    	return orderHisId;
    }

    @Override
    public void deleteOrderHistory(long orderHistoryId) {
        orderHisRepos.deleteOrderHistory(orderHistoryId);
    }

    @Override
    public void updateOrderHistory(OrderHistory orderHistory) {
        orderHisRepos.updateOrderHistory(orderHistory);
    }

    @Override
    public int countOrderHistory() {
        return orderHisRepos.countOrderHistory();
    }
}

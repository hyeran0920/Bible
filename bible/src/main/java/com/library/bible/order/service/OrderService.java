package com.library.bible.order.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.bible.order.model.Order;
import com.library.bible.order.repository.IOrderRepository;

@Service
public class OrderService implements IOrderService {

    @Autowired
    private IOrderRepository orderRepos;

    @Override
    public List<Order> getAllOrder() {
        return orderRepos.getAllOrder();
    }

    @Override
    public List<Order> getOrderByMemId(long memId) {
        return orderRepos.getOrderByMemId(memId);
    }

    @Override
    public List<Order> getOrderByHistoryId(long orderHistory) {
        return orderRepos.getOrderByHistoryId(orderHistory);
    }

    @Override
    public Order getOrderById(long orderId) {
        return orderRepos.getOrderById(orderId);
    }

    @Override
    public void insertOrder(Order order) {
        orderRepos.insertOrder(order);
    }

    @Override
    public void deleteOrder(long orderId) {
        orderRepos.deleteOrder(orderId);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepos.updateOrder(order);
    }

    @Override
    public int countAllOrders() {
        return orderRepos.countAllOrders();
    }

    @Override
    public int countMemOrders(long memId) {
        return orderRepos.countMemOrders(memId);
    }
}

package com.library.bible.order.service;

import java.util.List;

import com.library.bible.order.model.Order;

public interface IOrderService {
	List<Order> getAllOrder();
	List<Order> getOrderByMemId(long memId);
	List<Order> getOrderByHistoryId(long orderHistory);
	Order getOrderById(long orderId);
	
	void insertOrder(Order order);
	void deleteOrder(long orderId);
	void updateOrder(Order order);
	
	int countAllOrders();
	int countMemOrders(long memId);
}

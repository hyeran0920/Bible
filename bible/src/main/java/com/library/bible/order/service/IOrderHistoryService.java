package com.library.bible.order.service;

import java.util.List;

import com.library.bible.order.model.OrderHistory;

public interface IOrderHistoryService {
	
	List<OrderHistory> getAllOrderHistory();
	List<OrderHistory>getOrderHistoryByMemId(long memId);
	OrderHistory getOrderHistoryById(long orderHistoryId);
	
	void insertOrderHistory(OrderHistory orderHistory);
	void deleteOrderHistory(long orderHistoryId);
	void updateOrderHistory(OrderHistory orderHistory);
	
	int countOrderHistory();
	
}

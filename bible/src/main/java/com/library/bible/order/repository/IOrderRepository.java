package com.library.bible.order.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.order.model.Order;

@Mapper
@Repository
public interface IOrderRepository {
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

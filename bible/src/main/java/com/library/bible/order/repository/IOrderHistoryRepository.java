package com.library.bible.order.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.library.bible.order.model.OrderHistory;

@Mapper
@Repository
public interface IOrderHistoryRepository {
	List<OrderHistory> getAllOrderHistory();
	List<OrderHistory>getOrderHistoryByMemId(long memId);
	OrderHistory getOrderHistoryById(long orderHistoryId);
	
	void insertOrderHistory(OrderHistory orderHistory);
	void deleteOrderHistory(long orderHistoryId);
	void updateOrderHistory(OrderHistory orderHistory);
	
	int countOrderHistory();
}

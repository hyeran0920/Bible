package com.library.bible.order.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderHistoryMapper {

	void insertOrderHistory(Long memId, Long addressId, String receivedName, String paymentMethod);

	Long getLastInsertId();
    
}
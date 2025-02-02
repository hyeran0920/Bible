package com.library.bible.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.library.bible.order.OrderHistory;
import com.library.bible.order.OrderPageItemDTO;

@Mapper
public interface OrderMapper {

    void insertOrder(OrderPageItemDTO order);

    List<OrderPageItemDTO> getOrdersByMemId(Long memId); 

    void insertOrderHistory(OrderHistory orderHistory);
}


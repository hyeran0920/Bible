package com.library.bible.order.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.library.bible.order.dto.OrderPreviewDTO;

@Mapper
public interface OrderMapper {

    
    List<OrderPreviewDTO> findCartItemsByIds(@Param("cartIds") List<Integer> cartIds);

    // 주문 테이블에 데이터 삽입
    void insertOrder(@Param("cartIds") List<Integer> cartIds, @Param("orderHistoryId") Long orderHistoryId);

    // 주문 후 장바구니에서 해당 아이템 삭제
    void deleteCartItems(@Param("cartIds") List<Integer> cartIds);
}


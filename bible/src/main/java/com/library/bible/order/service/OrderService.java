package com.library.bible.order.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.library.bible.order.dto.OrderPreviewDTO;
import com.library.bible.order.mapper.OrderMapper;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderHistoryService orderHistoryService;

    public OrderService(OrderMapper orderMapper, OrderHistoryService orderHistoryService) {
        this.orderMapper = orderMapper;
        this.orderHistoryService = orderHistoryService;
    }

    /**
     * 주문 미리보기 데이터 조회
     */
    public List<OrderPreviewDTO> getOrderPreview(List<Integer> cartIds) {
        return orderMapper.findCartItemsByIds(cartIds);
    }

    /**
     * 주문 확정 (선택한 상품을 주문 테이블에 삽입)
     */
    @Transactional
    public void confirmOrder(List<Integer> cartIds, Long memId, Long addressId, String receivedName, String paymentMethod) {
        // 주문 내역 생성 및 order_history_id 반환
        Long orderHistoryId = orderHistoryService.createOrderHistory(memId, addressId, receivedName, paymentMethod);

        // 주문 테이블에 데이터 삽입
        orderMapper.insertOrder(cartIds, orderHistoryId);
        
        // 주문 완료 후, 장바구니에서 해당 항목 삭제
        orderMapper.deleteCartItems(cartIds);
    }

	public void confirmOrder(List<Integer> cartIds) {
		
	}
}

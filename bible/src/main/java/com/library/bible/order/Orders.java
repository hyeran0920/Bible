package com.library.bible.order;

import org.apache.ibatis.type.Alias;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Alias("orders")
public class Orders {
	private Long id;  // 주문 ID (PK)
    private String memId;  // 회원 ID
    private Integer totalPrice;  // 총 가격
    private String receivedName;  // 수령인 이름
    private String address;  // 배송 주소

    // 기본 생성자 필요
    public Orders() {}
}

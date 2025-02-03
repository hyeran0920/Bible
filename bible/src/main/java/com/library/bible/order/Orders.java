package com.library.bible.order;

import org.apache.ibatis.type.Alias;

@Alias("orders")
public class Orders {
	private Long id;  // 주문 ID (PK)
    private String memId;  // 회원 ID
    private Integer totalPrice;  // 총 가격
    private String receivedName;  // 수령인 이름
    private String address;  // 배송 주소

    // 기본 생성자 필요
    public Orders() {}

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMemId() { return memId; }
    public void setMemId(String memId) { this.memId = memId; }

    public Integer getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Integer totalPrice) { this.totalPrice = totalPrice; }

    public String getReceivedName() { return receivedName; }
    public void setReceivedName(String receivedName) { this.receivedName = receivedName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

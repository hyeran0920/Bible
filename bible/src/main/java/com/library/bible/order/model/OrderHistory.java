package com.library.bible.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistory {
	private long orderHistoryId;
	private long memId;
	private long addressId;
	private String orderHistoryDate;
	private long orderHistoryTotalPrice;
	private String orderHistoryReceivedName;
	private String orderPaymentMethod;
	private char orderPaymentStatus='f';
}

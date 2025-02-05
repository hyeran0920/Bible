package com.library.bible.order.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
	private long orderId;
	private long bookId;
	private long orderHistoryId;
	private long bookCount;
}

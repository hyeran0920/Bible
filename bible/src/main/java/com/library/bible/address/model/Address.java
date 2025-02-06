package com.library.bible.address.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
	private long addressId;
    private long memId;
    private String postcode;
	private String address;
	private String detailAddress;
	private String receiverName;
	private String receiverPhone;
	private int defaultAddress;
}

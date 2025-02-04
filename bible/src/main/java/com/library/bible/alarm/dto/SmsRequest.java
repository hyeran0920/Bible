package com.library.bible.alarm.dto;

import com.library.bible.cart.dto.CartRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsRequest {
	 private String to;
	 private String text;
}
package com.library.bible.order;

public class OrderPageItemDTO {
	
	private int bookId;
	
	private int sellingPrice;
	
	private int bookCount;
	
	//만듦
	private int totalPrice;
	
	public int getBookId() {
		return bookId;
	}
	
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
	public int getSellingPrice() {
		return sellingPrice;
	}
	
	public void setSellingPrice(int sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	
	public int getBookCount() {
		return bookCount;
	}

	public void setBookCount(int bookCount) {
		this.bookCount = bookCount;
	}
	
	public int getTotalPrice() {
		return totalPrice;
	}
	
	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	public void initSaleTotal() {
		this.totalPrice = this.sellingPrice*this.bookCount;
	}

}

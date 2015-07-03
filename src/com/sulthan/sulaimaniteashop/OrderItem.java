package com.sulthan.sulaimaniteashop;

public class OrderItem {
	int orderId;
	String date;
	String totalPrice;
	public OrderItem(int orderId, String date, String totalPrice) {
		super();
		this.orderId = orderId;
		this.date = date;
		this.totalPrice = totalPrice;
	}
	public int getOrderId() {
		return orderId;
	}
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}

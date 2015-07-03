package com.sulthan.sulaimaniteashop;

public class FoodItem {
	int id;
	String itemName;
	double price;
	boolean selected = false;
	int quantity;
	
	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public FoodItem(int id, String itemName, double price){
		this.id = id;
		this.itemName = itemName;
		this.price = price;
		this.quantity = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}

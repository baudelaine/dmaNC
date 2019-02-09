package com.dma.web;

import java.util.ArrayList;
import java.util.List;

public class Dimension {

	String name = "";
	List<String> orders = new ArrayList<String>();
	List<String> bks = new ArrayList<String>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getOrders() {
		return orders;
	}
	public void setOrders(List<String> orders) {
		this.orders = orders;
	}
	public List<String> getBks() {
		return bks;
	}
	public void setBks(List<String> bks) {
		this.bks = bks;
	}
	
}

package com.dma.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dimension {

	String name = "";
	List<Map<String, String>> orders = new ArrayList<Map<String, String>>();
	List<Map<String, String>> bks = new ArrayList<Map<String, String>>();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Map<String, String>> getOrders() {
		return orders;
	}
	public void setOrders(List<Map<String, String>> orders) {
		this.orders = orders;
	}
	public List<Map<String, String>> getBks() {
		return bks;
	}
	public void setBks(List<Map<String, String>> bks) {
		this.bks = bks;
	}
	
}

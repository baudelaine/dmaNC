package com.dma.web;

import java.util.ArrayList;
import java.util.List;

public class DimensionDetails {

	String qsFinalName = "";
	List<String> orders = new ArrayList<String>();
	List<String> BKs = new ArrayList<String>();

	public String getQsFinalName() {
		return qsFinalName;
	}
	public void setQsFinalName(String qsFinalName) {
		this.qsFinalName = qsFinalName;
	}
	public List<String> getOrders() {
		return orders;
	}
	public void setOrders(List<String> orders) {
		this.orders = orders;
	}
	public List<String> getBKs() {
		return BKs;
	}
	public void setBKs(List<String> bKs) {
		BKs = bKs;
	}
	
}

package com.dma.web;

public class Dimension {

	String name = "";
	DimensionDetails dimensionDetails = new DimensionDetails();

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public DimensionDetails getDimensionDetails() {
		return dimensionDetails;
	}
	public void setDimensionDetails(DimensionDetails dimensionDetails) {
		this.dimensionDetails = dimensionDetails;
	}
	
}

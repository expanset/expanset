package com.expanset.samples.complex.services;

public class StockQuote {

	private final String date; 
	
	private final double value;
	
	public StockQuote(String date, double value) {
		this.date = date;
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public double getValue() {
		return value;
	}
}

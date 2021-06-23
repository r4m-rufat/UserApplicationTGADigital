package com.kivitool.currencyend.models;

public class CurrencyResponse{
	private String license;
	private Rates rates;
	private String disclaimer;
	private int timestamp;
	private String base;

	public String getLicense(){
		return license;
	}

	public Rates getRates(){
		return rates;
	}

	public String getDisclaimer(){
		return disclaimer;
	}

	public int getTimestamp(){
		return timestamp;
	}

	public String getBase(){
		return base;
	}
}

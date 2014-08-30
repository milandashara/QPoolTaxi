package com.example.model;

import java.io.Serializable;

public class TaxiStand implements Serializable{

	private Double lat;
	private Double lng;
	private String name;
	private Integer status;
	private String taxiStandId;
	
	public String getTaxiStandId() {
		return taxiStandId;
	}
	public void setTaxiStandId(String taxiStandId) {
		this.taxiStandId = taxiStandId;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLng() {
		return lng;
	}
	public void setLng(Double lng) {
		this.lng = lng;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
}

package com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FindPartner implements Serializable{

	private TaxiStand taxiStand;
	private User user;
	private Double lat;
	private Double lng;
	private String destination;
	private Integer status=1;
	private List<Partner> partners=new ArrayList<Partner>();;
	
	public List<Partner> getPartners() {
		return partners;
	}
	public void setPartners(List<Partner> partners) {
		this.partners = partners;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	private String numOfmem;
	private String memreq="0";
	public TaxiStand getTaxiStand() {
		return taxiStand;
	}
	public void setTaxiStand(TaxiStand taxiStand) {
		this.taxiStand = taxiStand;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
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
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getNumOfmem() {
		return numOfmem;
	}
	public void setNumOfmem(String numOfmem) {
		this.numOfmem = numOfmem;
	}
	public String getMemreq() {
		return memreq;
	}
	public void setMemreq(String memreq) {
		this.memreq = memreq;
	}
	
	
	
}

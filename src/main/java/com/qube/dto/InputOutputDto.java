package com.qube.dto;

public class InputOutputDto implements Comparable<Object>{
	String theatre;
	String sizeofDelivery;
	String deliveryId;
	String isPossible;
	Integer price;
	public String getTheatre() {
		return theatre;
	}
	public void setTheatre(String theatre) {
		this.theatre = theatre;
	}
	public String getSizeofDelivery() {
		return sizeofDelivery;
	}
	public void setSizeofDelivery(String sizeofDelivery) {
		this.sizeofDelivery = sizeofDelivery;
	}
	public String getDeliveryId() {
		return deliveryId;
	}
	public void setDeliveryId(String deliveryId) {
		this.deliveryId = deliveryId;
	}
	public String getIsPossible() {
		return isPossible;
	}
	public void setIsPossible(String isPossible) {
		this.isPossible = isPossible;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	@Override
	public int compareTo(Object arg0) {
		InputOutputDto InputOutputDto = (InputOutputDto) arg0; 
        return this.price - InputOutputDto.getPrice() ;
		
	}
	
	
	
}

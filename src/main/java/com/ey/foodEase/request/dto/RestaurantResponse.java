package com.ey.foodEase.request.dto;

import java.math.BigDecimal;

public class RestaurantResponse {

    private long id;
    private String name;
    private String address;
    private BigDecimal rating;
    private Long ownerId;

    public RestaurantResponse() {}

    public RestaurantResponse(long id, String name, String address, BigDecimal rating, Long ownerId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.ownerId = ownerId;
    }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public BigDecimal getRating() {
		return rating;
	}

	public void setRating(BigDecimal rating) {
		this.rating = rating;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

    
}
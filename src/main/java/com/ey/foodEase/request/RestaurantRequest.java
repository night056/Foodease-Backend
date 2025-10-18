package com.ey.foodEase.request;

public class RestaurantRequest {
	private String name;
	private String address;
	private long ownerId;

	public RestaurantRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RestaurantRequest(String name, String address, long ownerId) {
		super();
		this.name = name;
		this.address = address;
		this.ownerId = ownerId;
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

	public long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}
}

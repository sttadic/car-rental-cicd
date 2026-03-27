package edu.stjepan.carrental.dto;

import java.math.BigDecimal;

public class CarDTO {
	private Long id;
	private String make;
	private String model;
	private String registrationNumber;
	private int year;
	private BigDecimal dailyRate;
	private int totalBookings;

	public CarDTO() {

	}

	public CarDTO(Long id, String make, String model, String registrationNumber, int year, BigDecimal dailyRate,
			int totalBookings) {
		this.id = id;
		this.make = make;
		this.model = model;
		this.registrationNumber = registrationNumber;
		this.year = year;
		this.dailyRate = dailyRate;
		this.totalBookings = totalBookings;
	}

	public Long getId() {
		return id;
	}

	public String getMake() {
		return make;
	}

	public String getModel() {
		return model;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public int getYear() {
		return year;
	}

	public BigDecimal getDailyRate() {
		return dailyRate;
	}

	public int getTotalBookings() {
		return totalBookings;
	}
}

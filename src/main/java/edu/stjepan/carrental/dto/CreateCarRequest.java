package edu.stjepan.carrental.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;

public class CreateCarRequest {

	@NotBlank
	private String make;

	@NotBlank
	private String model;

	@NotBlank
	private String registrationNumber;

	@Min(1990)
	@Max(2050)
	private int year;

	@DecimalMin("0.0")
	private BigDecimal dailyRate;

	public CreateCarRequest() {
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

}

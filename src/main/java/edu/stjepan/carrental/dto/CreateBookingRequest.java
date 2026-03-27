package edu.stjepan.carrental.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.*;

public class CreateBookingRequest {

	@NotNull
	private Long carId;

	@NotBlank
	private String customerName;

	@Email
	@NotBlank
	private String customerEmail;

	@NotNull
	private LocalDate startDate;

	@NotNull
	private LocalDate endDate;

	@NotBlank
	private String status;

	public CreateBookingRequest() {
	}

	public Long getCarId() {
		return carId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getCustomerEmail() {
		return customerEmail;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public String getStatus() {
		return status;
	}

}

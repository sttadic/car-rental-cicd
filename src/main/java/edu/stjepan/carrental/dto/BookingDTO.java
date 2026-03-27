package edu.stjepan.carrental.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookingDTO {
	private Long id;
	private Long carId;
	private String customerName;
	private String customerEmail;
	private LocalDate startDate;
	private LocalDate endDate;
	private BigDecimal totalAmount;
	private String status;

	public BookingDTO() {
	}

	public BookingDTO(Long id, Long carId, String customerName, String customerEmail, LocalDate startDate,
			LocalDate endDate, BigDecimal totalAmount, String status) {
		super();
		this.id = id;
		this.carId = carId;
		this.customerName = customerName;
		this.customerEmail = customerEmail;
		this.startDate = startDate;
		this.endDate = endDate;
		this.totalAmount = totalAmount;
		this.status = status;
	}

	public Long getId() {
		return id;
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

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public String getStatus() {
		return status;
	}

}

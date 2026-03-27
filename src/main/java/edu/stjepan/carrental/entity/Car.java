package edu.stjepan.carrental.entity;

import java.math.BigDecimal;
import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name = "cars")
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String make;

	@Column(nullable = false)
	private String model;

	@Column(name = "registration_number", nullable = false, unique = true)
	private String registrationNumber;

	@Column(name = "manufacture_year", nullable = false)
	private int year;

	@Column(name = "daily_rate", nullable = false, precision = 10, scale = 2)
	private BigDecimal dailyRate;

	@OneToMany(mappedBy = "car", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Booking> bookings = new ArrayList<>();

	public Car() {
	}

	public Car(String make, String model, String registrationNumber, int year, BigDecimal dailyRate) {
		this.make = make;
		this.model = model;
		this.registrationNumber = registrationNumber;
		this.year = year;
		this.dailyRate = dailyRate;
	}

	public Long getId() {
		return id;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public BigDecimal getDailyRate() {
		return dailyRate;
	}

	public void setDailyRate(BigDecimal dailyRate) {
		this.dailyRate = dailyRate;
	}

	public List<Booking> getBookings() {
		return bookings;
	}

	// Helpers
	public void addBooking(Booking booking) {
		bookings.add(booking);
		booking.setCar(this);
	}

	public void removeBooking(Booking booking) {
		bookings.remove(booking);
		booking.setCar(null);
	}

}

package edu.stjepan.carrental.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.stjepan.carrental.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

	// Get all bookings for specific car
	List<Booking> findByCarId(Long carId);

	// Filter bookings by date range
	List<Booking> findByStartDateBetween(LocalDate start, LocalDate end);
}

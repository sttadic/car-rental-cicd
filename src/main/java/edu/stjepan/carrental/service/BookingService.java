package edu.stjepan.carrental.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.*;

import edu.stjepan.carrental.dto.*;

public interface BookingService {

	BookingDTO createBooking(CreateBookingRequest request);

	BookingDTO getBookingById(Long id);

	List<BookingDTO> getBookingsForCar(Long carId);

	Page<BookingDTO> getAllBookings(Pageable pageable);

	List<BookingDTO> getBookingsByDateRange(LocalDate start, LocalDate end);

	void deleteBooking(Long id);
}

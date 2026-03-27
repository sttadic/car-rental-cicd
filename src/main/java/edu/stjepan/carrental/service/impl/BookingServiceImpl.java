package edu.stjepan.carrental.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import edu.stjepan.carrental.dto.*;
import edu.stjepan.carrental.entity.*;
import edu.stjepan.carrental.exception.ResourceNotFoundException;
import edu.stjepan.carrental.mapper.BookingMapper;
import edu.stjepan.carrental.repository.*;
import edu.stjepan.carrental.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {
	private final BookingRepository bookingRepository;
	private final CarRepository carRepository;

	@Autowired
	public BookingServiceImpl(BookingRepository bookingRepository, CarRepository carRepository) {
		this.bookingRepository = bookingRepository;
		this.carRepository = carRepository;
	}

	@Override
	public BookingDTO createBooking(CreateBookingRequest request) {
		Car car = carRepository.findById(request.getCarId())
				.orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + request.getCarId()));

		if (request.getEndDate().isBefore(request.getStartDate())) {
			throw new IllegalArgumentException("End date cannot be before start date.");
		}

		long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
		if (days == 0) {
			throw new IllegalArgumentException("Booking must be at least 1 day.");
		}

		BigDecimal totalAmount = car.getDailyRate().multiply(BigDecimal.valueOf(days));

		Booking booking = new Booking(car, request.getCustomerName(), request.getCustomerEmail(),
				request.getStartDate(), request.getEndDate(), totalAmount, request.getStatus());

		Booking saved = bookingRepository.save(booking);
		return BookingMapper.toDTO(saved);
	}

	@Override
	public BookingDTO getBookingById(Long id) {
		Booking booking = bookingRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id));
		return BookingMapper.toDTO(booking);
	}

	@Override
	public List<BookingDTO> getBookingsForCar(Long carId) {
		return bookingRepository.findByCarId(carId).stream().map(BookingMapper::toDTO).toList();
	}

	@Override
	public Page<BookingDTO> getAllBookings(Pageable pageable) {
		return bookingRepository.findAll(pageable).map(BookingMapper::toDTO);
	}

	@Override
	public List<BookingDTO> getBookingsByDateRange(LocalDate start, LocalDate end) {
		return bookingRepository.findByStartDateBetween(start, end).stream().map(BookingMapper::toDTO).toList();
	}

	@Override
	public void deleteBooking(Long id) {
		if (!bookingRepository.existsById(id)) {
			throw new ResourceNotFoundException("Booking not found with id: " + id);
		}
		bookingRepository.deleteById(id);

	}

}

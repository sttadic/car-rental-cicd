package edu.stjepan.carrental.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.stjepan.carrental.dto.*;
import edu.stjepan.carrental.entity.*;
import edu.stjepan.carrental.exception.ResourceNotFoundException;
import edu.stjepan.carrental.repository.*;
import edu.stjepan.carrental.service.impl.BookingServiceImpl;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

	@Mock
	private BookingRepository bookingRepository;

	@Mock
	private CarRepository carRepository;

	@InjectMocks
	private BookingServiceImpl bookingService;

	// Only truly shared state
	private Car car;

	@BeforeEach
	void setUp() throws Exception {
		car = new Car("Toyota", "Corolla", "REG-001", 2022, new BigDecimal("50.00"));
		var idField = Car.class.getDeclaredField("id");
		idField.setAccessible(true);
		idField.set(car, 1L);
	}

	@Test
	void createBooking_whenCarExistsAndDatesAreValid_shouldCreateBooking() throws Exception {
		LocalDate startDate = LocalDate.of(2025, 8, 1);
		LocalDate endDate = LocalDate.of(2025, 8, 3);
		BigDecimal expectedTotal = car.getDailyRate().multiply(BigDecimal.valueOf(2));

		CreateBookingRequest request = new CreateBookingRequest();
		setField(request, "carId", 1L);
		setField(request, "customerName", "John Doe");
		setField(request, "customerEmail", "john.doe@example.com");
		setField(request, "startDate", startDate);
		setField(request, "endDate", endDate);
		setField(request, "status", "CREATED");

		Booking savedBooking = new Booking(car, "John Doe", "john.doe@example.com",
				startDate, endDate, expectedTotal, "CREATED");

		when(carRepository.findById(1L)).thenReturn(Optional.of(car));
		when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

		BookingDTO result = bookingService.createBooking(request);

		assertNotNull(result);
		assertEquals(1L, result.getCarId());
		assertEquals("John Doe", result.getCustomerName());
		assertEquals("john.doe@example.com", result.getCustomerEmail());
		assertEquals(startDate, result.getStartDate());
		assertEquals(endDate, result.getEndDate());
		assertEquals(expectedTotal, result.getTotalAmount());
		assertEquals("CREATED", result.getStatus());

		verify(carRepository, times(1)).findById(1L);
		verify(bookingRepository, times(1)).save(any(Booking.class));
	}

	@Test
	void createBooking_whenCarDoesNotExist_shouldThrowResourceNotFoundException() throws Exception {
		CreateBookingRequest request = new CreateBookingRequest();
		setField(request, "carId", 999L);
		setField(request, "customerName", "John Doe");
		setField(request, "customerEmail", "john.doe@example.com");
		setField(request, "startDate", LocalDate.of(2025, 8, 1));
		setField(request, "endDate", LocalDate.of(2025, 8, 3));
		setField(request, "status", "CREATED");

		when(carRepository.findById(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(
				ResourceNotFoundException.class,
				() -> bookingService.createBooking(request));

		assertEquals("Car not found with id: 999", exception.getMessage());
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void createBooking_whenEndDateBeforeStartDate_shouldThrowIllegalArgumentException() throws Exception {
		CreateBookingRequest request = new CreateBookingRequest();
		setField(request, "carId", 1L);
		setField(request, "customerName", "John Doe");
		setField(request, "customerEmail", "john.doe@example.com");
		setField(request, "startDate", LocalDate.of(2025, 8, 5));
		setField(request, "endDate", LocalDate.of(2025, 8, 1));
		setField(request, "status", "CREATED");

		when(carRepository.findById(1L)).thenReturn(Optional.of(car));

		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> bookingService.createBooking(request));

		assertEquals("End date cannot be before start date.", exception.getMessage());
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	@Test
	void createBooking_whenStartDateEqualsEndDate_shouldThrowIllegalArgumentException() throws Exception {
		LocalDate sameDate = LocalDate.of(2025, 8, 1);

		CreateBookingRequest request = new CreateBookingRequest();
		setField(request, "carId", 1L);
		setField(request, "customerName", "John Doe");
		setField(request, "customerEmail", "john.doe@example.com");
		setField(request, "startDate", sameDate);
		setField(request, "endDate", sameDate);
		setField(request, "status", "CREATED");

		when(carRepository.findById(1L)).thenReturn(Optional.of(car));

		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> bookingService.createBooking(request));

		assertEquals("Booking must be at least 1 day.", exception.getMessage());
		verify(bookingRepository, never()).save(any(Booking.class));
	}

	private void setField(Object target, String fieldName, Object value)
			throws NoSuchFieldException, IllegalAccessException {
		var field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(target, value);
	}
}

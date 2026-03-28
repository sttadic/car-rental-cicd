package edu.stjepan.carrental.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import edu.stjepan.carrental.dto.BookingDTO;
import edu.stjepan.carrental.dto.CreateBookingRequest;
import edu.stjepan.carrental.entity.Car;
import edu.stjepan.carrental.exception.ResourceNotFoundException;
import edu.stjepan.carrental.repository.CarRepository;

@SpringBootTest
@Transactional
class BookingServiceIT {

  @Autowired
  private BookingService bookingService;

  @Autowired
  private CarRepository carRepository;

  @Test
  void createBooking_whenCarExistsAndDatesAreValid_shouldPersistAndReturnBooking() {
    Car car = new Car("Toyota", "Camry", "INT-001", 2022, new BigDecimal("50.00"));
    Car savedCar = carRepository.save(car);

    LocalDate startDate = LocalDate.of(2025, 8, 1);
    LocalDate endDate = LocalDate.of(2025, 8, 5);

    CreateBookingRequest request = buildRequest(savedCar.getId(), "John Doe",
        "john.doe@example.com", startDate, endDate, "CREATED");

    BookingDTO result = bookingService.createBooking(request);
    assertNotNull(result.getId());
    assertEquals(savedCar.getId(), result.getCarId());
    assertEquals("John Doe", result.getCustomerName());
    assertEquals("john.doe@example.com", result.getCustomerEmail());
    assertEquals(startDate, result.getStartDate());
    assertEquals(endDate, result.getEndDate());
    assertEquals("CREATED", result.getStatus());
    assertNotNull(result.getTotalAmount()); // was persisted
    assertTrue(result.getTotalAmount() // sanity check only
        .compareTo(BigDecimal.ZERO) > 0);
  }

  @Test
  void createBooking_whenCarDoesNotExist_shouldThrowResourceNotFoundException() {
    CreateBookingRequest request = buildRequest(999L, "John Doe",
        "john.doe@example.com", LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 5), "CREATED");

    assertThrows(ResourceNotFoundException.class,
        () -> bookingService.createBooking(request));
  }

  private CreateBookingRequest buildRequest(Long carId, String name, String email,
      LocalDate start, LocalDate end, String status) {
    try {
      CreateBookingRequest request = new CreateBookingRequest();
      setField(request, "carId", carId);
      setField(request, "customerName", name);
      setField(request, "customerEmail", email);
      setField(request, "startDate", start);
      setField(request, "endDate", end);
      setField(request, "status", status);
      return request;
    } catch (Exception e) {
      throw new RuntimeException("Failed to build request", e);
    }
  }

  private void setField(Object target, String fieldName, Object value) throws Exception {
    var field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}

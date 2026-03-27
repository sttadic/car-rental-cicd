package edu.stjepan.carrental.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import edu.stjepan.carrental.dto.BookingDTO;
import edu.stjepan.carrental.entity.*;

class BookingMapperTest {

	@Test
	void toDTO_shouldMapAllFieldsCorrectly() {
		// Arrange
		Car car = new Car("Toyota", "Corolla", "REG-001", 2018, new BigDecimal("45.00"));

		// Set the car's id via reflection since there's no setter for id
		try {
			var idField = Car.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(car, 1L);
		} catch (Exception e) {
			fail("Could not set car id: " + e.getMessage());
		}

		Booking booking = new Booking(car, "Alice Johnson", "alice@example.com", LocalDate.of(2025, 1, 10),
				LocalDate.of(2025, 1, 12), new BigDecimal("90.00"), "CONFIRMED");

		try {
			var idField = Booking.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(booking, 1L);
		} catch (Exception e) {
			fail("Could not set booking id: " + e.getMessage());
		}

		// Act
		BookingDTO dto = BookingMapper.toDTO(booking);

		// Assert
		assertNotNull(dto);
		assertEquals(1L, dto.getId());
		assertEquals(1L, dto.getCarId());
		assertEquals("Alice Johnson", dto.getCustomerName());
		assertEquals("alice@example.com", dto.getCustomerEmail());
		assertEquals(LocalDate.of(2025, 1, 10), dto.getStartDate());
		assertEquals(LocalDate.of(2025, 1, 12), dto.getEndDate());
		assertEquals(new BigDecimal("90.00"), dto.getTotalAmount());
		assertEquals("CONFIRMED", dto.getStatus());
	}
}

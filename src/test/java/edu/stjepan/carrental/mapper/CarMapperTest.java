package edu.stjepan.carrental.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import edu.stjepan.carrental.dto.CarDTO;
import edu.stjepan.carrental.entity.Car;

class CarMapperTest {

  @Test
  void toDTO_shouldMapAllFieldsCorrectly() {
    // Arrange
    Car car = new Car("Honda", "Civic", "REG-100", 2021, new BigDecimal("65.00"));

    try {
      var idField = Car.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(car, 7L);
    } catch (Exception e) {
      fail("Could not set car id: " + e.getMessage());
    }

    // Act
    CarDTO dto = CarMapper.toDTO(car);

    // Assert
    assertNotNull(dto);
    assertEquals(7L, dto.getId());
    assertEquals("Honda", dto.getMake());
    assertEquals("Civic", dto.getModel());
    assertEquals("REG-100", dto.getRegistrationNumber());
    assertEquals(2021, dto.getYear());
    assertEquals(new BigDecimal("65.00"), dto.getDailyRate());
    assertEquals(0, dto.getTotalBookings());
  }
}

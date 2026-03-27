package edu.stjepan.carrental.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.stjepan.carrental.dto.CarDTO;
import edu.stjepan.carrental.dto.CreateCarRequest;
import edu.stjepan.carrental.entity.Car;
import edu.stjepan.carrental.exception.ResourceNotFoundException;
import edu.stjepan.carrental.repository.CarRepository;
import edu.stjepan.carrental.service.impl.CarServiceImpl;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

  @Mock
  private CarRepository carRepository;

  @InjectMocks
  private CarServiceImpl carService;

  @Test
  void getCarById_WhenCarExists_ShouldReturnCarDTO() throws Exception {
    // Arrange
    Long carId = 1L;
    Car car = new Car("Toyota", "Corolla", "ABC-123", 2022, new BigDecimal("49.99"));
    Field idField = Car.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(car, carId);

    when(carRepository.findById(carId)).thenReturn(Optional.of(car));

    // Act
    CarDTO result = carService.getCarById(carId);

    // Assert
    assertNotNull(result);
    assertEquals(carId, result.getId());
    assertEquals("Toyota", result.getMake());
    assertEquals("Corolla", result.getModel());
    assertEquals("ABC-123", result.getRegistrationNumber());
    assertEquals(2022, result.getYear());
    assertEquals(new BigDecimal("49.99"), result.getDailyRate());
    assertEquals(0, result.getTotalBookings());

    verify(carRepository, times(1)).findById(carId);
  }

  @Test
  void getCarById_WhenCarDoesNotExist_ShouldThrowResourceNotFoundException() {
    // Arrange
    Long carId = 999L;
    when(carRepository.findById(carId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> carService.getCarById(carId));

    assertEquals("Car not found with id: " + carId, exception.getMessage());

    verify(carRepository, times(1)).findById(carId);
  }

  @Test
  void createCar_whenRegistrationIsUnique_shouldCreateAndReturnCarDTO() throws Exception {
    CreateCarRequest request = new CreateCarRequest();
    setField(request, "make", "Honda");
    setField(request, "model", "Civic");
    setField(request, "registrationNumber", "NEW-001");
    setField(request, "year", 2023);
    setField(request, "dailyRate", new BigDecimal("55.00"));

    Car savedCar = new Car("Honda", "Civic", "NEW-001", 2023, new BigDecimal("55.00"));
    Field idField = Car.class.getDeclaredField("id");
    idField.setAccessible(true);
    idField.set(savedCar, 5L);

    when(carRepository.existsByRegistrationNumber("NEW-001")).thenReturn(false);
    when(carRepository.save(any(Car.class))).thenReturn(savedCar);

    CarDTO result = carService.createCar(request);

    assertNotNull(result);
    assertEquals(5L, result.getId());
    assertEquals("Honda", result.getMake());
    assertEquals("Civic", result.getModel());
    assertEquals("NEW-001", result.getRegistrationNumber());
    assertEquals(2023, result.getYear());
    assertEquals(new BigDecimal("55.00"), result.getDailyRate());

    verify(carRepository).existsByRegistrationNumber("NEW-001");
    verify(carRepository).save(any(Car.class));
  }

  @Test
  void createCar_whenRegistrationAlreadyExists_shouldThrowIllegalArgumentException() throws Exception {
    CreateCarRequest request = new CreateCarRequest();
    setField(request, "make", "Honda");
    setField(request, "model", "Civic");
    setField(request, "registrationNumber", "DUP-001");
    setField(request, "year", 2023);
    setField(request, "dailyRate", new BigDecimal("55.00"));

    when(carRepository.existsByRegistrationNumber("DUP-001")).thenReturn(true);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> carService.createCar(request));

    assertEquals("Car with this registration number already exists", exception.getMessage());
    verify(carRepository, never()).save(any(Car.class));
  }

  @Test
  void deleteCar_whenCarExists_shouldDeleteWithoutException() {
    when(carRepository.existsById(1L)).thenReturn(true);

    assertDoesNotThrow(() -> carService.deleteCar(1L));

    verify(carRepository).deleteById(1L);
  }

  @Test
  void deleteCar_whenCarDoesNotExist_shouldThrowResourceNotFoundException() {
    when(carRepository.existsById(999L)).thenReturn(false);

    ResourceNotFoundException exception = assertThrows(
        ResourceNotFoundException.class,
        () -> carService.deleteCar(999L));

    assertEquals("Car not found with id: 999", exception.getMessage());
    verify(carRepository, never()).deleteById(anyLong());
  }

  private void setField(Object target, String fieldName, Object value)
      throws NoSuchFieldException, IllegalAccessException {
    var field = target.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(target, value);
  }
}

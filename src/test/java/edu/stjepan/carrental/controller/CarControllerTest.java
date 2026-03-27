package edu.stjepan.carrental.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.stjepan.carrental.dto.CarDTO;
import edu.stjepan.carrental.dto.CreateCarRequest;
import edu.stjepan.carrental.exception.GlobalExceptionHandler;
import edu.stjepan.carrental.exception.ResourceNotFoundException;
import edu.stjepan.carrental.service.CarService;

@WebMvcTest(controllers = CarController.class)
@Import(GlobalExceptionHandler.class)
class CarControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private CarService carService;

  @Test
  void createCar_whenRequestValid_shouldReturn201() throws Exception {
    String validJson = """
        {
          "make": "Toyota",
          "model": "Corolla",
          "registrationNumber": "ABC-123",
          "year": 2022,
          "dailyRate": 49.99
        }
        """;

    CarDTO stubResponse = new CarDTO(1L, "Toyota", "Corolla", "ABC-123", 2022,
        new BigDecimal("49.99"), 0);

    when(carService.createCar(any(CreateCarRequest.class))).thenReturn(stubResponse);

    mockMvc.perform(post("/api/cars")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.make").value("Toyota"))
        .andExpect(jsonPath("$.model").value("Corolla"))
        .andExpect(jsonPath("$.registrationNumber").value("ABC-123"));

    verify(carService).createCar(any(CreateCarRequest.class));
  }

  @Test
  void createCar_whenMakeMissing_shouldReturn400WithValidationError() throws Exception {
    String missingMakeJson = """
        {
          "model": "Corolla",
          "registrationNumber": "ABC-123",
          "year": 2022,
          "dailyRate": 49.99
        }
        """;

    mockMvc.perform(post("/api/cars")
        .contentType(MediaType.APPLICATION_JSON)
        .content(missingMakeJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("make")));

    verify(carService, never()).createCar(any(CreateCarRequest.class));
  }

  @Test
  void getCar_whenCarExists_shouldReturn200() throws Exception {
    CarDTO stubResponse = new CarDTO(1L, "Toyota", "Corolla", "ABC-123", 2022,
        new BigDecimal("49.99"), 0);

    when(carService.getCarById(1L)).thenReturn(stubResponse);

    mockMvc.perform(get("/api/cars/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.make").value("Toyota"));

    verify(carService).getCarById(1L);
  }

  @Test
  void getCar_whenCarDoesNotExist_shouldReturn404() throws Exception {
    when(carService.getCarById(999L))
        .thenThrow(new ResourceNotFoundException("Car not found with id: 999"));

    mockMvc.perform(get("/api/cars/999"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.error").value("Not Found"))
        .andExpect(jsonPath("$.message").value("Car not found with id: 999"));

    verify(carService).getCarById(999L);
  }

  @Test
  void deleteCar_whenCarExists_shouldReturn204() throws Exception {
    doNothing().when(carService).deleteCar(1L);

    mockMvc.perform(delete("/api/cars/1"))
        .andExpect(status().isNoContent());

    verify(carService).deleteCar(1L);
  }
}

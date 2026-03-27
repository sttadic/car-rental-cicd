package edu.stjepan.carrental.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.stjepan.carrental.dto.BookingDTO;
import edu.stjepan.carrental.dto.CreateBookingRequest;
import edu.stjepan.carrental.exception.GlobalExceptionHandler;
import edu.stjepan.carrental.service.BookingService;

@WebMvcTest(controllers = BookingController.class)
@Import(GlobalExceptionHandler.class)
class BookingControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private BookingService bookingService;

  @Test
  void createBooking_whenCarIdMissing_shouldReturn400WithCarIdError() throws Exception {
    String missingCarIdJson = """
        {
          "customerName": "John Doe",
          "customerEmail": "john@example.com",
          "startDate": "2025-08-01",
          "endDate": "2025-08-05",
          "status": "CREATED"
        }
        """;

    mockMvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(missingCarIdJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.path").value("/api/bookings"))
        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("carId")));

    verify(bookingService, never()).createBooking(any(CreateBookingRequest.class));
  }

  @Test
  void createBooking_whenEmailInvalid_shouldReturn400WithEmailError() throws Exception {
    String invalidEmailJson = """
        {
          "carId": 1,
          "customerName": "John Doe",
          "customerEmail": "not-an-email",
          "startDate": "2025-08-01",
          "endDate": "2025-08-05",
          "status": "CREATED"
        }
        """;

    mockMvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidEmailJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Validation Error"))
        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("customerEmail")));

    verify(bookingService, never()).createBooking(any(CreateBookingRequest.class));
  }

  @Test
  void createBooking_whenDateMalformed_shouldReturn400() throws Exception {
    String malformedDateJson = """
        {
          "carId": 1,
          "customerName": "John Doe",
          "customerEmail": "john@example.com",
          "startDate": "not-a-date",
          "endDate": "2025-08-05",
          "status": "CREATED"
        }
        """;

    mockMvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(malformedDateJson))
        .andExpect(status().isBadRequest());

    verify(bookingService, never()).createBooking(any(CreateBookingRequest.class));
  }

  @Test
  void createBooking_whenRequestValid_shouldReturn201AndCallService() throws Exception {
    String validJson = """
        {
          "carId": 1,
          "customerName": "John Doe",
          "customerEmail": "john@example.com",
          "startDate": "2025-08-01",
          "endDate": "2025-08-05",
          "status": "CREATED"
        }
        """;

    BookingDTO stubResponse = new BookingDTO(
        10L, 1L, "John Doe", "john@example.com",
        java.time.LocalDate.of(2025, 8, 1),
        java.time.LocalDate.of(2025, 8, 5),
        new java.math.BigDecimal("200.00"),
        "CREATED");

    when(bookingService.createBooking(any(CreateBookingRequest.class)))
        .thenReturn(stubResponse);

    mockMvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(validJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.carId").value(1))
        .andExpect(jsonPath("$.customerName").value("John Doe"));

    verify(bookingService).createBooking(any(CreateBookingRequest.class));
  }
}
